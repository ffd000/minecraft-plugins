using MiNET;

namespace Siegenet.Creator
{
    abstract public class CreatorStage
    {
        protected readonly Player _player;

        protected CreatorStage(Player player) => _player = player;

        abstract public void Cleanup();
        abstract public void Finish();
    }
}
