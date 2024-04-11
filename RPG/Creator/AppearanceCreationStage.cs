using System;
using System.Threading;
using MiNET;
using MiNET.Net;
using MiNET.Entities;
using MiNET.Utils;
using MiNET.Utils.Skins;
using RaceInfo = Siegenet.Creator.CharacterInfo.RaceInfo;

namespace Siegenet.Creator
{
    public class AppearanceCreationStage : CreatorStage
    {
        private readonly PlayerMob _npc;
        private readonly Timer _task;

        private int _yaw = 180;

        private string _raceName = "Cyndor";

        private int _raceIndex = 1;
        private int _baseIndex = 0;
        private int _eyesIndex = 0;
        
        public AppearanceCreationStage(Player player) : base(player)
        {
            var coordinates = player.KnownPosition;

            _npc = new PlayerMob("§5" + _raceName, player.Level)
            {
                Skin = new Skin
                {
                    Slim = false,
                    SkinId = _raceName + Guid.NewGuid(),
                    SkinData = CharacterInfo.Races[RaceInfo.Cyndor].BaseSkin,
                    CapeData = new byte[0],
                    SkinGeometryName = "geometry.cyndor",
                    SkinGeometry = CharacterCreator.Models[RaceInfo.Cyndor]
                },
                KnownPosition = new PlayerLocation(coordinates.X, coordinates.Y + 1.8, coordinates.Z + 2, 180, 0),
            };
            _npc.SpawnEntity();

            _task = new Timer(RotateNpc, null, 100, 100);
        }

        public override void Cleanup()
        {
            _npc.DespawnEntity();
            _task.Dispose();
        }

        public override void Finish()
        {
            Skin skin = _npc.Skin;

            _player.AddToPlayerList(); // Crash fix

            _player.Skin = skin;

            McpePlayerSkin updateSkin = McpePlayerSkin.CreateObject();
            updateSkin.uuid = _player.ClientUuid;
            updateSkin.skinId = skin.SkinId;
            updateSkin.skinData = skin.SkinData;
            updateSkin.capeData = skin.CapeData;
            updateSkin.geometryModel = skin.SkinGeometryName;
            updateSkin.geometryData = skin.SkinGeometry;
            _player.Level.RelayBroadcast(updateSkin);

            Cleanup();
            _player.Inventory.Clear();

            CharacterCreator.Queue[_player.EntityId] = new ClassSelectionStage(_player);
        }

        public void PreviousRace()
        {
            if (_raceIndex-- == 1) _raceIndex = CharacterCreator.RacesCount;

            ChangeRace();
        }

        public void NextRace()
        {
            if (_raceIndex++ == CharacterCreator.RacesCount) _raceIndex = 1;

            ChangeRace();
        }

        private void ChangeRace()
        {
            RaceInfo race = (RaceInfo)_raceIndex;
            _raceName = race.ToString();

            UpdateSkin(CharacterInfo.Races[race].BaseSkin, CharacterCreator.Models[race]);

            _npc.SetNameTag("§5" + _raceName);

            _yaw = 170;
        }

        public void ChangeBase()
        {
            if (++_baseIndex == 4) _baseIndex = 0;

            ChangePart(CharacterCreator.Bases[(RaceInfo)_raceIndex][_baseIndex]);
        }

        public void ChangeEyes()
        {
            if (++_eyesIndex == 4) _eyesIndex = 0;

            ChangePart(CharacterCreator.Eyes[(RaceInfo)_raceIndex][_eyesIndex]);
        }

        private void ChangePart(byte[] partData)
        {
            byte[] skinData = _npc.Skin.SkinData;
            CharacterCreator.ApplySkinLayer(ref skinData, partData);
            UpdateSkin(skinData, _npc.Skin.SkinGeometry);

            _yaw = 170;
        }

        private void UpdateSkin(byte[] skinData, string geometryData)
        {
            _npc.AddToPlayerList();

            Skin skin = _npc.Skin;
            skin.SkinId = _raceName + Guid.NewGuid();
            skin.SkinData = skinData;
            skin.SkinGeometryName = "geometry." + _raceName.ToLower();
            skin.SkinGeometry = geometryData;

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

        public void RotateNpc(object state)
        {
			_npc.LastUpdatedTime = DateTime.UtcNow;
            var position = _npc.KnownPosition;

			var package = McpeMovePlayer.CreateObject();
			package.runtimeEntityId = _npc.EntityId;
			package.x = position.X;
			package.y = position.Y;
			package.z = position.Z;
			package.yaw = _yaw;
			package.headYaw = _yaw;
			package.pitch = position.Pitch;
			package.mode = 0;

            _player.Level.RelayBroadcast(package);

            _yaw = _yaw == 360 ? 0 : _yaw += 10;
        }
    }
}