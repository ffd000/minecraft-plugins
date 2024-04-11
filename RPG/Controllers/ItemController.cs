using MiNET;
using MiNET.Character;
using MiNET.UI;
using Siegenet.Creator;
using System.Collections.Generic;

namespace Siegenet.Controllers
{
    public abstract class ItemController
    {
        public static void Init()
        {
            // Race selection and skin creation
			MiNetServer.ItemHandlers.Add("351:0", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].PreviousRace();
            });
            MiNetServer.ItemHandlers.Add("351:1", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].NextRace();
            });
            MiNetServer.ItemHandlers.Add("351:2", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].ChangeBase();
            });
            MiNetServer.ItemHandlers.Add("351:3", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].ChangeEyes();
            });
            // Confirmation
            MiNetServer.ItemHandlers.Add("351:4", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].Finish();
            });
            // Class selection
            MiNetServer.ItemHandlers.Add("351:5", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].PreviousClass();
            });
            MiNetServer.ItemHandlers.Add("351:6", (Player player) =>
            {
                CharacterCreator.Queue[player.EntityId].NextClass();
            });
            // Game
            MiNetServer.ItemHandlers.Add("345:0", (Player player) =>
            {
                string attributesList = ""; 
                foreach (KeyValuePair<AttributeType, CharacterAttribute> entry in player.CharacterAttributes)
                {
                    CharacterAttribute attribute = entry.Value;
                    attributesList += "§6" + attribute.Name + " §l§e" + attribute.Value + "\n§r§o§5" + attribute.Description + "§r\n\n";
                }

                player.SendForm(new CustomForm
                {
                    Title = "Your Characteristics",
                    Content = new List<CustomElement>()
                    {
                        new Label {Text = attributesList}
                    },
                });
            });
        }
    }
}