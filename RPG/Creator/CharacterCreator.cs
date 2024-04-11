using log4net;
using MiNET;
using MiNET.Items;
using MiNET.UI;
using MiNET.Utils.Skins;
using System.IO;
using System.Collections.Generic;
using System;
using RaceInfo = Siegenet.Creator.CharacterInfo.RaceInfo;

namespace Siegenet.Creator
{
    public class CharacterCreator
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(CharacterCreator));

        public static readonly Dictionary<long, dynamic> Queue = new Dictionary<long, dynamic>();  // actually Dictionary<long, CreatorStage> but the methods of derived classes are not recognized and it gives errors

        public static readonly Dictionary<RaceInfo, List<byte[]>> Bases = new Dictionary<RaceInfo, List<byte[]>>();
        public static readonly Dictionary<RaceInfo, List<byte[]>> Eyes = new Dictionary<RaceInfo, List<byte[]>>();

        public static readonly Dictionary<RaceInfo, string> Models = new Dictionary<RaceInfo, string>();

        public static readonly sbyte RacesCount;

        static CharacterCreator()
        {
            Array races = Enum.GetValues(typeof(RaceInfo));
            
            foreach (RaceInfo race in races)
            {
                LoadSkins(race, Path.Combine(Base.PluginDir, "skins"));
                LoadModels(race, Path.Combine(Base.PluginDir, "models"));
            }

            RacesCount = (sbyte)races.Length;
        }
        
        public static void Enter(Player player, CustomForm form)
        {
            player.Level.DespawnFromAll(player);

            Queue.Add(player.EntityId, new AppearanceCreationStage(player));
            
            short idx = 0;
			player.Inventory.Slots[idx++] = new ItemDye().SetCustomName("§3Previous Race", "View the previous character race.");
			player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 1 }.SetCustomName("§3Next Race", "View the next character race.");
			player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 2 }.SetCustomName("§3Change Base", "View the next base.");
			player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 3 }.SetCustomName("§3Change Eyes", "View the next eyes.");
			player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 4 }.SetCustomName("§aDone", "Confirm your character.");
			player.SendPlayerInventory();
        }

        public static void Confirm(Player player)
        {
            player.SendForm(new ModalForm
            {
                ExecuteAction = Queue[player.EntityId].HandleConfirmation,
                Title = Base.Lang.GetString("form_confirmcharacter_title"),
                Content = Base.Lang.GetString("form_confirmcharacter_content"),
                Button1 = "Yes",
                Button2 = "No"
            });
        }

        public static async void SaveData(Player player, int raceId)
        {
            if (await Controllers.PlayerController.AddPlayerData(player.ClientUuid, player.Username, (sbyte)raceId, player.CharacterAttributes))
            {
                await PlayerSkinManager.SaveSkin(player);

                player.Level.SpawnToAll(player);

                player.SendMessage("Successfully created your character.");
            }
            else
            {
                player.SendMessage("Could not create your character. An unknown error occured.");
            }
        }

        /*
         * The functions below prepare assets used by the Character Creator and provide
         * skin manipulation utilities.
         */

        public static void LoadSkins(RaceInfo race, string skinsDir)
        {
            Console.Write($"  Loading skins for {race}...");
            string raceSkinsDir = Path.Combine(skinsDir, race.ToString());

            Bases[race] = GetSkinsFromAllImagesInDirectory(Path.Combine(raceSkinsDir, "bases"));
            Eyes[race] = GetSkinsFromAllImagesInDirectory(Path.Combine(raceSkinsDir, "eyes"));
            Console.WriteLine(" done");
        }

        public static void LoadModels(RaceInfo race, string modelsDir)
        {
            Console.Write($"  Loading model for {race}...");
            using (StreamReader reader = new StreamReader(Path.Combine(modelsDir, race + ".json")))
            {
                Models.Add(race, reader.ReadToEnd());
            }
            Console.WriteLine(" done");
        }

        private static List<byte[]> GetSkinsFromAllImagesInDirectory(string dir)
        {
            List<byte[]> bytes = new List<byte[]>();
            foreach (string filename in Directory.GetFiles(dir, "*.png"))
            {
                bytes.Add(Skin.GetTextureFromFile(filename));
            }

            return bytes;
        }

        public static void ApplySkinLayer(ref byte[] baseLayer, byte[] newLayer)
        {
            for (int n = 0; n < newLayer.Length; n += 2)
            {
                if ((newLayer[n] | newLayer[n + 1]) != 0)
                {
                    baseLayer[n] = newLayer[n];
                    baseLayer[n + 1] = newLayer[n + 1];
                }
            }
        }
    }
}