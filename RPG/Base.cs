using log4net;
using MiNET;
using MiNET.Utils;
using MiNET.Plugins;
using MiNET.Plugins.Attributes;
using System.Configuration;
using System.Resources;
using System.Reflection;
using Siegenet.Creator;
using Siegenet.Controllers;
using MiNET.Net;

namespace Siegenet
{
    [Plugin(PluginName = "Base", Description = "Base plugin", PluginVersion = "0.1", Author = "ppopovabg")]
    public class Base : Plugin
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Base));

        public static readonly ResourceManager Lang = new ResourceManager("Base.strings", Assembly.GetExecutingAssembly());

        public static readonly string PluginDir = Config.GetProperty("PluginDirectory");

        public static readonly string ConnectionString = ConfigurationManager.ConnectionStrings["defaultConnection"].ConnectionString;

        protected override void OnEnable()
        {
            new CharacterCreator();

            ItemController.Init();
            RoleController.Init();
            //QuestController.Init();

            ResourcePackManager.PackStack = new ResourcePack(@"../../../../Siege.Base/bin/Debug/netcoreapp2.1/siege.zip");

            Context.Server.PlayerFactory.PlayerCreated += (sender, args) =>
            {
                Player player = args.Player;
                player.PlayerLeave += PlayerController.PlayerLeaveHandler;
            };

            Log.Info("Base enabled.");
        }

        [PacketHandler, Receive]
        public Packet PlayerInitializationHandler(McpeSetLocalPlayerAsInitializedPacket packet, Player player)
        {
#pragma warning disable CS4014
            PlayerController.InitPlayerDataAsync(player);

            return packet;
        }

        [PacketHandler, Receive]
        public Packet PlayerChatHandler(McpeText text, Player player)
        {
            // The player can't chat if their data hasn't been loaded yet.
            if (player.Role == null) return null;

            if (text.message.StartsWith("/")) return text;

            player.Level.BroadcastMessage(string.Format(player.Role.ChatFormat, player.Username, text.message), MessageType.Raw);

            return null;
        }
    }
}