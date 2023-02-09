import os
from pathlib import Path

from dotenv import load_dotenv


def get_project_root():
    return Path(__file__).parent.parent.parent


def get_env_path():
    return os.path.join(get_project_root(), ".env")


def get_image_from_static(name_image: str) -> str:
    if not name_image.endswith(".png") and not name_image.endswith(".jpg"):
        raise Exception()
    return os.path.join(get_project_root(), f"static/{name_image}")


def get_database_root() -> str:
    return os.path.join(get_project_root(), "data/checklist_database.db")


def get_config_root() -> str:
    return os.path.join(get_project_root(), "config.json")


def get_logger_path() -> str:
    return os.path.join(get_project_root(), "static/logger.txt")


def get_names_path() -> str:
    return os.path.join(get_project_root(), "data/checklist.names")


def get_weights_path() -> str:
    return os.path.join(get_project_root(), "data/checklist-tiny_final.weights")


def get_cfg_path() -> str:
    return os.path.join(get_project_root(), "data/checklist-tiny.cfg")


def load_env():
    load_dotenv(get_env_path())


load_env()
