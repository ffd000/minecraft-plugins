using MiNET;
using MiNET.Net;
using MiNET.Utils.Skins;
using System.IO;
using System.IO.Compression;
using System.Threading.Tasks;

namespace Siegenet
{
    class PlayerSkinManager
    {
        private static readonly string SavedSkinsDir = Path.Combine(Base.PluginDir, "playerskins");

        public static async Task SaveSkin(Player player)
        {
            player.SendMessage("Saving your skin...");

            byte[] skin = player.Skin.SkinData;
            // Compress data
            MemoryStream output = new MemoryStream();
            using (DeflateStream dstream = new DeflateStream(output, CompressionLevel.Optimal))
            {
                await dstream.WriteAsync(skin, 0, skin.Length);
            }
            // Save to file
            await File.WriteAllBytesAsync(Path.Combine(SavedSkinsDir, (player.Username.ToLower() + ".dat")), output.ToArray());

            player.SendMessage("Skin saved.");
        }

        public static async void LoadSkin(Player player)
        {
            player.SendMessage("Loading your skin...");

            string path = Path.Combine(SavedSkinsDir, player.Username.ToLower() + ".dat");
            if (File.Exists(path))
            {
                // Decompress data
                var bytes = await File.ReadAllBytesAsync(path);
                MemoryStream input = new MemoryStream(bytes);
                MemoryStream output = new MemoryStream();
                using (DeflateStream dstream = new DeflateStream(input, CompressionMode.Decompress))
                {
                    await dstream.CopyToAsync(output);
                }
                byte[] skinData = output.ToArray();

                player.AddToPlayerList(); // Crash fix

                player.Skin.SkinData = skinData;
                Skin skin = player.Skin;

                McpePlayerSkin updateSkin = McpePlayerSkin.CreateObject();
                updateSkin.uuid = player.ClientUuid;
                updateSkin.skinId = skin.SkinId;
                updateSkin.skinData = skinData;
                updateSkin.capeData = skin.CapeData;
                updateSkin.geometryModel = skin.SkinGeometryName;
                updateSkin.geometryData = skin.SkinGeometry;
                player.Level.RelayBroadcast(updateSkin);

                player.SendMessage("Skin loaded.");
            }
            else
            {
                player.SendMessage("Could not load your skin.");
            }
        }
    }
}
