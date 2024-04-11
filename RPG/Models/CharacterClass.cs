using MiNET.Character;
using MiNET.Utils.Skins;
using System.Collections.Generic;
using System.IO;

namespace Siegenet.Models
{
    public abstract class CharacterClass
    {
        public readonly string Name;
        public readonly List<AttributeModifier> Modifiers;
        public readonly byte[] SkinData;

        protected CharacterClass(string name, List<AttributeModifier> modifiers)
        {
            Name = name;
            Modifiers = modifiers;
            SkinData = Skin.GetTextureFromFile(Path.Combine(Base.PluginDir, "class skins/" + name + ".png"));
        }
    }
}
