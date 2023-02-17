import asyncio

from uvicorn import Config, Server

from app.my_requests.commands import app
from app.database.databasehander import database_handler


def main() -> None:
    loop = asyncio.new_event_loop()
    asyncio.run_coroutine_threadsafe(database_handler.run_process_requests(), loop)
    print("Port changed")
    config = Config(app=app, loop=loop, host="0.0.0.0", port=8000)
    server = Server(config)
    loop.run_until_complete(server.serve())
    loop.close()


if __name__ == '__main__':
    main()
