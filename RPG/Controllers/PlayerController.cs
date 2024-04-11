using log4net;
using MiNET;
using MiNET.Items;
using MiNET.Net;
using MiNET.UI;
using MiNET.Character;
using MySql.Data.MySqlClient;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Data;
using System.Threading.Tasks;
using Siegenet.Creator;

namespace Siegenet.Controllers
{
    class PlayerController
    {
		private static readonly ILog Log = LogManager.GetLogger(typeof(Base));

        public static void PlayerLeaveHandler(object o, PlayerEventArgs eventArgs)
        {
			long playerId = eventArgs.Player.EntityId;
            if (CharacterCreator.Queue.ContainsKey(playerId))
            {
                CharacterCreator.Queue[playerId].Cleanup();
                CharacterCreator.Queue.Remove(playerId);
            }
        }

        public static async void InitPlayerDataAsync(Player player)
        {
            player.SendMessage("Loading your player data...");

            var loginTable = await GetPlayerDataAsync(player.ClientUuid);
            if (loginTable != null && loginTable.Rows.Count > 0)
            {
                var loginData = loginTable.Rows[0];
                player.Role = RoleController.Roles[(sbyte)loginData["role_id"]];
                player.Race = (sbyte)loginData["race_id"];
                //player.Experience = (float)loginData["char_exp"];
                player.CharacterAttributes = JsonConvert.DeserializeObject<Dictionary<AttributeType, CharacterAttribute>>((string)loginData["attributes"]);
                
                player.SetNoAi(false);

                player.SendMessage("Your data was loaded successfully.");

                player.Inventory.Slots[0] = new ItemCompass().SetCustomName("Character Info", "View your character information");
                player.SendPlayerInventory();
            }
            else
            {
                player.SendForm(new CustomForm
                {
                    Title = Base.Lang.GetString("form_welcome_title"),
                    Content = new List<CustomElement>()
                    {
                        new Label {Text = Base.Lang.GetString("form_welcome_content")}
                    },
                    ExecuteAction = CharacterCreator.Enter,
                });
            }
        }

        public static async Task<DataTable> GetPlayerDataAsync(UUID uuid)
        {
            using (var conn = new MySqlConnection(Base.ConnectionString))
            using (var query = new MySqlCommand("SELECT race_id, role_id, char_exp, attributes FROM players WHERE uuid=@uuid", conn))
            {
                try
                {
                    query.Parameters.AddWithValue("@uuid", uuid.ToString());
                    await conn.OpenAsync();

                    var adapter = new MySqlDataAdapter(query);
                    var data = new DataTable();
                    adapter.Fill(data); // The adapter has no truly asynchronous Fill method.

                    return data;
                }
                catch (MySqlException e)
                {
                    Log.Error("Internal server error: " + e.Message);

                    return null;
                }
            }
        }

        public static async Task<bool> AddPlayerData(UUID uuid, string playerName, sbyte raceId, Dictionary<AttributeType, CharacterAttribute> attributes)
        {
            using (MySqlConnection conn = new MySqlConnection(Base.ConnectionString))
            using (MySqlCommand query = new MySqlCommand("INSERT INTO players (uuid, player_name, race_id, role_id, char_exp, attributes) VALUES (@uuid, @player_name, @race_id, 1, 0, @attributes)", conn))
            {   
                try
                {
                    query.Parameters.AddWithValue("@uuid", uuid.ToString());
                    query.Parameters.AddWithValue("@player_name", playerName);
                    query.Parameters.AddWithValue("@race_id", raceId);
                    query.Parameters.AddWithValue("@attributes", JsonConvert.SerializeObject(attributes));
                    await conn.OpenAsync();
                    await query.ExecuteNonQueryAsync();

                    return true;
                }
                catch (MySqlException e)
                {
                    Log.Error("Internal server error: " + e.Message);

                    return false;
                }
            }
        }
    }
}
