using System;
using MiNET;
using MiNET.Net;
using MiNET.Entities;
using MiNET.Utils.Skins;
using MiNET.Utils;
using MiNET.Items;
using MiNET.UI;
using MiNET.Character;
using ClassInfo = Siegenet.Creator.CharacterInfo.ClassInfo;

namespace Siegenet.Creator
{
    public class ClassSelectionStage : CreatorStage
    {
        private readonly PlayerMob _npc;
        private readonly Hologram _hologram;

        private string _className = "Knight";

        private int _classIndex = 1;
        
        public ClassSelectionStage(Player player) : base(player)
        {
            var coordinates = player.KnownPosition;

            _npc = new PlayerMob("§5" + _className, player.Level)
            {
                Skin = new Skin
                {
                    Slim = false,
                    SkinId = _className + Guid.NewGuid(),
                    SkinData = CharacterInfo.Classes[ClassInfo.Knight].SkinData,
                    CapeData = new byte[0],
                    SkinGeometryName = "",
                    SkinGeometry = ""
                },
                KnownPosition = new PlayerLocation(coordinates.X, coordinates.Y, coordinates.Z + 2, 180, 0),
            };
            _npc.SpawnEntity();

            _hologram = new Hologram("§l§6Stat Bonuses\n§r" + UpdateAttributeBonuses(ClassInfo.Knight), player.Level)
            {
                KnownPosition = new PlayerLocation(coordinates.X + 2, coordinates.Y + 2, coordinates.Z + 2)
            };
            _hologram.SpawnEntity();

            short idx = 0;
            player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 5 }.SetCustomName("§3Previous Class", "View the previous character class.");
            player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 6 }.SetCustomName("§3Next Class", "View the next character class.");
            player.Inventory.Slots[idx++] = new ItemDye() { Metadata = 4 }.SetCustomName("§aDone", "Confirm your character.");
            player.SendPlayerInventory();
        }

        public override void Cleanup()
        {
            _npc.DespawnEntity();
            _hologram.DespawnEntity();
        }

        public override void Finish()
        {
            _player.SendForm(new ModalForm
            {
                ExecuteAction = HandleConfirmation,
                Title = Base.Lang.GetString("form_confirmcharacter_title"),
                Content = Base.Lang.GetString("form_confirmcharacter_content"),
                Button1 = "Yes",
                Button2 = "No"
            });
        }

        public void PreviousClass()
        {
            if (_classIndex-- == 1) _classIndex = CharacterCreator.RacesCount;

            ChangeClass();
        }

        public void NextClass()
        {
            if (_classIndex++ == CharacterCreator.RacesCount) _classIndex = 1;

            ChangeClass();
        }

        private void ChangeClass()
        {
            ClassInfo charClass = (ClassInfo)_classIndex;
            _className = charClass.ToString();

            UpdateSkin(CharacterInfo.Classes[charClass].SkinData);

            _npc.SetNameTag("§5" + _className);

            _hologram.SetNameTag("§l§6Stat Bonuses\n§r" + UpdateAttributeBonuses(charClass));
        }

        private string UpdateAttributeBonuses(ClassInfo charClass)
        {
            string attributeBonuses = "";
            bool doLineBreak = false;
            foreach (AttributeModifier modifier in CharacterInfo.Classes[charClass].Modifiers)
            {
                string newLine = doLineBreak ? "\n" : "";
                attributeBonuses += newLine + "§7- §b" + modifier.Type.ToString() + " §l§d" + modifier.Value.ToString("+0;-#");
                doLineBreak = true;
            }

            return attributeBonuses;
        }

        public void HandleConfirmation(Player player, ModalForm form)
        {
            Cleanup();

            ClassInfo charClass = (ClassInfo)_classIndex;

            foreach (AttributeModifier modifier in CharacterInfo.Classes[charClass].Modifiers)
            {
                player.CharacterAttributes[modifier.Type].RawModifiers.Add(modifier);
                player.CharacterAttributes[modifier.Type].Calculate();
            }

            CharacterCreator.SaveData(player, player.Race);

            player.SetNoAi(false); // Player is now free!

            player.Inventory.Clear();
            player.Inventory.Slots[0] = new ItemCompass().SetCustomName("§6Character Info", "View your character information");
            player.SendPlayerInventory();
        }

        private void UpdateSkin(byte[] skinData)
        {
            _npc.AddToPlayerList();

            Skin skin = _npc.Skin;
            skin.SkinId = _className + Guid.NewGuid();
            skin.SkinData = skinData;

            McpePlayerSkin updateSkin = McpePlayerSkin.CreateObject();
            updateSkin.uuid = _npc.ClientUuid;
            updateSkin.skinId = skin.SkinId;
            updateSkin.skinData = skin.SkinData;
            updateSkin.capeData = skin.CapeData;
            updateSkin.geometryModel = skin.SkinGeometryName;
            updateSkin.geometryData = skin.SkinGeometry;
            _npc.Level.RelayBroadcast(updateSkin);

            _npc.RemoveFromPlayerList();
        }
    }
}