using log4net;
using MySql.Data.MySqlClient;
using System.Collections.Generic;
using System.Linq;
using MiNET;

namespace Siegenet.Controllers
{
    class RoleController
    {
		private static readonly ILog Log = LogManager.GetLogger(typeof(Base));

        public static Dictionary<sbyte, Role> Roles { get; set; } = new Dictionary<sbyte, Role>();

        public static async void Init()
        {
            using (var conn = new MySqlConnection(Base.ConnectionString))
            using (var query = new MySqlCommand("SELECT id, name, chat_format, permissions FROM roles", conn))
            {
                try
                {
                    await conn.OpenAsync();

                    var reader = await query.ExecuteReaderAsync();
                    while (await reader.ReadAsync())
                    {
                        sbyte id = (sbyte)reader.GetValue(0);
                        Roles.Add(id, new Role
                        (
                            id,
                            reader.GetString(1),
                            reader.GetString(2),
                            reader.GetString(3).Split(";").ToList()
                        ));
                    }
                }
                catch (MySqlException e)
                {
                    Log.Error("Internal server error: " + e.Message);
                }
            }
        }
    }
}
