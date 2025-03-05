import requests

# サーバーのURL
BASE_URL = "http://localhost:8080/add"

# クエリパラメータを指定
params = {
    "a": 5,
    "b": 10
}

# GETリクエストを送信
response = requests.get(BASE_URL, params=params)

# レスポンスの表示
if response.status_code == 200:
    print("Response:", response.text)
else:
    print("Error:", response.status_code, response.text)
