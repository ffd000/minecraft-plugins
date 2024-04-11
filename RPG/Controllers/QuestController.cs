using MiNET.Quest;
using System.Collections.Generic;
using System.Linq;

namespace Siegenet.Controllers
{
    public abstract class QuestController
    {
        public static Dictionary<short, Quest> Quests = new Dictionary<short, Quest>();

        //public static void Init()
        //{
        //    RegisterQuest(new Quest
        //    {
        //        Title = "Sample quest",
        //        Description = "Sample description",
        //        ExpReward = 500
        //    });
        //    RegisterQuest(new Quest
        //    {
        //        Title = "Sample quest 2",
        //        Description = "Sample description",
        //        Requirements = new QuestRequirements
        //        {
        //            Level = 5
        //        },
        //        ExpReward = 700
        //    });
        //    RegisterQuest(new Quest
        //    {
        //        Title = "Sample quest 3",
        //        Description = "Sample description",
        //        Requirements = new QuestRequirements
        //        {
        //            Level = 10,
        //            Quests = new List<short>(new short[] { 1, 2 })
        //        },
        //        /*ItemRewards = new List<MiNET.Items.Item>(...),*/
        //        ExpReward = 1500
        //    });
        //}

        //private static void RegisterQuest(Quest quest)
        //{
        //    short Id = (short)(Quests.Keys.Max() + 1);
        //    quest.Id = Id.ToString();
        //    Quests.Add(Id, quest);
        //}
    }
}
