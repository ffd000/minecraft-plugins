using System.Collections.Generic;
using Siegenet.Models;
using Siegenet.Character;

namespace Siegenet.Creator
{
    public class CharacterInfo
    {
        public enum RaceInfo
        {
            Cyndor = 1,
            Vulparian = 2,
        }

        public enum ClassInfo
        {
            Knight = 1,
            Mage = 2,
        }

        public static readonly Dictionary<RaceInfo, CharacterRace> Races = new Dictionary<RaceInfo, CharacterRace>
        {
            { RaceInfo.Cyndor, new CharacterRace("race1", "desc1") },
            { RaceInfo.Vulparian, new CharacterRace("race2", "desc2") },
        };

        public static readonly Dictionary<ClassInfo, CharacterClass> Classes = new Dictionary<ClassInfo, CharacterClass>
        {
            { ClassInfo.Knight, new CharacterClass("Knight",
            new List<AttributeModifier>{
                new AttributeModifier(AttributeType.Strength, 5),
                new AttributeModifier(AttributeType.Fortitude, 5),
                new AttributeModifier(AttributeType.Charisma, 5),
                new AttributeModifier(AttributeType.WeightCapacity, 20)
            })},
            { ClassInfo.Mage, new CharacterClass("Mage",
            new List<AttributeModifier>{
                new AttributeModifier(AttributeType.Intellect, 10),
                new AttributeModifier(AttributeType.Alignment, 5),
                new AttributeModifier(AttributeType.Assessment, 5),
                new AttributeModifier(AttributeType.Attunement, 5),
            })},
        };
    }
}