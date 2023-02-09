from starlette.testclient import TestClient

from app.commands import app
from app.core.pathutils import get_image_from_static


def test_upload_photos() -> None:
    path_image = get_image_from_static("test_image.png")

    client = TestClient(app)

    with open(path_image, "rb") as fr:
        image_binary = fr.read()

    response = client.post("/check_photo_with_list_of_products",
                           params={"user_id": "my_test_user_id"},
                           files={"photo_bytes": image_binary})

    assert response.status_code == 200
