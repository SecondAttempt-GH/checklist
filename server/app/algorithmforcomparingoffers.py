import typing

from app.core.config import get_config


class AlgorithmForComparingOffers:
    """
        Алгоритм сравнения продуктов пользователя
        с продуктов, полученный с камеры
    """

    def __init__(self) -> None:
        config = get_config()
        # Минимальная длина для того, чтобы считаться словом
        self._min_len_words = config.pca.min_len_words
        # Размер подстроки при сравнении двух слов (от 1 до min_len_words)
        self._sub_token_len = config.pca.sub_token_len
        # Порог принятия нечеткой эквивалентности между двумя словами
        self._threshold_word = config.pca.threshold_word
        # Порог принятия нечеткой эквивалентности между двумя предложениями
        self._threshold_sentence = config.pca.threshold_sentence

    def check(self, first_product: str, second_product: str) -> bool:
        return self._threshold_sentence <= self.__calculate_fuzzy_equal_value(first_product, second_product)

    def __calculate_fuzzy_equal_value(self, first_product: str, second_product) -> float:
        if self.__is_null_or_white_space(first_product) and self.__is_null_or_white_space(second_product):
            return 1.0

        if self.__is_null_or_white_space(first_product) or self.__is_null_or_white_space(second_product):
            return 0.0

        normalized_first_product = self.__normalization_data(first_product)
        normalized_second_product = self.__normalization_data(second_product)

        first_tokens = self.__get_tokens(normalized_first_product)
        second_tokens = self.__get_tokens(normalized_second_product)

        fuzzy_equals_tokens = self.__get_fuzzy_equals_tokens(first_tokens, second_tokens)

        equals_count = len(fuzzy_equals_tokens)
        first_count = len(first_tokens)
        second_count = len(second_tokens)

        result_value = (1.0 * equals_count) / (first_count + second_count - equals_count)
        return result_value

    def __get_fuzzy_equals_tokens(self, first_tokens: typing.List[str], second_tokens: typing.List[str]) -> typing.List[str]:
        equal_tokens = []
        used_tokens = [False for i in range(len(second_tokens))]
        for i in range(len(first_tokens)):
            for j in range(len(second_tokens)):
                if used_tokens[j] is False:
                    if self.__is_tokens_fuzzy_equal(first_tokens[i], second_tokens[j]):
                        equal_tokens.append(first_tokens[i])
                        used_tokens[j] = True
                        break
        return equal_tokens

    def __is_tokens_fuzzy_equal(self, first_token: str, second_token: str) -> bool:
        equal_sub_tokens_count = 0
        used_tokens = [False for i in range(len(second_token) + 1 - self._sub_token_len)]
        for i in range(len(first_token) - self._sub_token_len + 1):
            sub_token_first = first_token[i:i + self._sub_token_len]
            for j in range(len(second_token) - self._sub_token_len + 1):
                if used_tokens[j] is False:
                    sub_token_second = second_token[j:j + self._sub_token_len]
                    if sub_token_first == sub_token_second:
                        equal_sub_tokens_count += 1
                        used_tokens[j] = True
                        break

        sub_token_first_count = len(first_token) - self._sub_token_len + 1
        sub_token_second_count = len(second_token) - self._sub_token_len + 1

        tanimoto = (1.0 * equal_sub_tokens_count) / (sub_token_first_count + sub_token_second_count)
        return self._threshold_word <= tanimoto

    @staticmethod
    def __normalization_data(data: str) -> str:
        """
            Нормализация данных
            Удаляем из строки все что не равно буквам, цифрам и пробелу
        :param data:
        :return:
        """
        return_data = ""
        for char in data.lower():
            if char.isdigit() or char.isalpha() or char == ' ':
                return_data += char

        return return_data

    def __get_tokens(self, data: str) -> typing.List[str]:
        """
            Избавляемся от союзов и тп
            Удаляем все слова, которые меньше нужного нам минимума
        :param data:
        :return:
        """
        tokens = []
        words = data.split()
        for word in words:
            if len(word) >= self._min_len_words:
                tokens.append(word)
        return tokens

    @staticmethod
    def __is_null_or_white_space(text: str) -> bool:
        return text is None or text == ''
