using Newtonsoft.Json;
using System.Collections.Generic;

namespace Siegenet.Character
{
    public enum AttributeType
    {
        Strength,
        Fortitude,
        Agility,
        Intellect,
        Charisma,
        WeightCapacity,
        Attunement,
        Alignment,
        Assessment,
    }

    public class CharacterAttribute
    {
        public readonly string Name;
        public float Value { get; set; }
        [JsonIgnore]
        public string Description { get; protected set; }
        [JsonIgnore]
        public readonly List<AttributeModifier> RawModifiers = new List<AttributeModifier>();
        //public readonly Dictionary<EffectModifierType, Modifier> EffectModifiers = new Dictionary<EffectModifierType, Modifier>();

        public CharacterAttribute(string name) => Name = name;

        public void Calculate()
        {
            foreach (AttributeModifier modifier in RawModifiers)
            {
                if (modifier.Multiplier == 0f)
                {
                    Value += modifier.Value;
                }
                else
                {
                    Value *= modifier.Multiplier;
                }
            }
        }
    }
}
