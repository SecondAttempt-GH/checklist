import time
import typing

import cv2 as cv
import easyocr
import numpy as np

from app.core.config import get_config
from app.core.pathutils import get_names_path, get_weights_path, get_cfg_path


class PriceTagData:
    def __init__(self) -> None:
        self._found_text: typing.Optional[str] = None
        self._time_spend: typing.Optional[float] = None

    @property
    def found_text(self) -> str:
        return self._found_text

    @property
    def time_spent(self):
        return self._time_spend

    def set_found_text(self, value: str) -> None:
        self._found_text = value

    def set_time_spent(self, value: float) -> None:
        self._time_spend = value


class DeterminantTextOfPriceTag:
    def __init__(self) -> None:
        self._names_path = get_names_path()
        self._weights_path = get_weights_path()
        self._cfg_path = get_cfg_path()
        self._config = get_config()

        self._names = self.__get_names_class()
        self.__init_model()

    def __init_model(self) -> None:
        net = cv.dnn.readNet(self._weights_path, self._cfg_path)

        net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV)
        net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU)

        self._model = cv.dnn_DetectionModel(net)
        self._model.setInputParams(size=(416, 416), scale=1 / 255, swapRB=True)

    def to_determine(self, image_bytes) -> PriceTagData:
        time_start = time.time()
        data = PriceTagData()

        image = np.asarray(bytearray(image_bytes), dtype="uint8")

        image = cv.imdecode(image, cv.IMREAD_COLOR)

        classes, scores, boxes = self._model.detect(image, self._config.ml.conf_threshold, self._config.ml.nms_threshold)

        title_image = None
        for class_id, score, box in zip(classes, scores, boxes):
            x, y, w, h = box

            class_name = self._names[class_id]
            crop_image = image[y:y + h, x:x + w]
            if class_name == "title":
                title_image = crop_image
                break

        if title_image is None:
            return data

        reader = easyocr.Reader(self._config.ml.languages, gpu=True)
        bounds = reader.readtext(title_image)
        time_end = time.time()

        found_text = ' '.join([bound[1] for bound in bounds])
        data.set_found_text(found_text)
        data.set_time_spent(round(time_end - time_start, 3))
        return data

    def __get_names_class(self) -> list:
        with open(self._names_path, 'r', encoding="utf-8") as fr:
            return [cname.strip() for cname in fr.readlines()]
