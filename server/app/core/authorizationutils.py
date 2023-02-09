import random
import string

length_token = 30


def generate_token() -> str:
    global length_token

    letters = string.ascii_lowercase
    rand_string = ''.join(random.choice(letters) for i in range(length_token))
    return rand_string
