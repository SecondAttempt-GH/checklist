import sqlite3

from app.core.pathutils import get_database_root
from app.database.tables import users_table, shopping_list_table


class DatabaseCreator:
    def __init__(self) -> None:
        self._database_path = get_database_root()
        self._connector = sqlite3.connect(self._database_path)

    def create_tables(self) -> None:
        cursor = self._connector.cursor()
        cursor.execute(shopping_list_table)
        cursor.execute(users_table)
        self._connector.commit()


if __name__ == "__main__":
    DatabaseCreator().create_tables()
