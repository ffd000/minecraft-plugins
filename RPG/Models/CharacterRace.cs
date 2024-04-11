using System.IO;
using MiNET.Utils.Skins;

namespace Siegenet.Races
{
    public abstract class CharacterRace
    {
        public readonly string Name;
        public readonly byte[] BaseSkin;
        public readonly string Description;

        protected CharacterRace(string name, string description)
        {
            Name = name;
            BaseSkin = Skin.GetTextureFromFile(Path.Combine(Base.PluginDir, "skins/" + name + "/base.png"));
            Description = description;
        }
    }
}
