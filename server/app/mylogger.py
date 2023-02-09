import datetime

from app.core.config import get_config
from app.core.pathutils import get_logger_path


class MyLogger:
    def __init__(self, write_file: bool = False, overwrite_file: bool = False) -> None:
        self._write_file = write_file
        if overwrite_file:
            self.__overwrite_file()

    def info(self, message: str, method: str = None) -> None:
        self.__process_ready_message("INFO", message, method)

    def warning(self, message: str, method: str = None) -> None:
        self.__process_ready_message("WARNING", message, method)

    def error(self, message: str, method: str = None) -> None:
        self.__process_ready_message("ERROR", message, method)

    def __process_ready_message(self, type_command: str, message: str, method: str = None) -> None:
        if method is not None:
            ready_message = f"[{type_command.upper()}] - [{method}] - [{datetime.datetime.now()}] - [{message}]"
        else:
            ready_message = f"[{type_command.upper()}] - [{method}] - [{datetime.datetime.now()}] - [{message}]"

        if self._write_file:
            self.__write_message_in_file(ready_message)
        print(ready_message)

    @staticmethod
    def __overwrite_file() -> None:
        path_file = get_logger_path()
        with open(path_file, 'w', encoding="utf-8") as fw:
            fw.write("")

    @staticmethod
    def __write_message_in_file(message: str) -> None:
        path_file = get_logger_path()
        with open(path_file, 'a', encoding="utf-8") as fw:
            fw.write(message + '\n')


config = get_config()
my_logger = MyLogger(config.code.write_in_file_logs, config.code.overwrite_logs)
