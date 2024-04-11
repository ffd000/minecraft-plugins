namespace Siegenet.Character
{
    public enum EffectModifierType
    {
        Potion = 1,
        Spell = 2,
        Item = 3,
    }

    public class AttributeModifier
    {
        public readonly AttributeType Type;
        public readonly float Value;
        public readonly float Multiplier;

        public AttributeModifier(AttributeType type, float value, float multiplier = 0f)
        {
            Type = type;
            Value = value;
            Multiplier = multiplier;
        }
    }
}
