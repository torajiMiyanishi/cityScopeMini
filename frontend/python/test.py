import requests

# Javaサーバーのエンドポイント
url = "http://localhost:8080/calculate"

# 送信するパラメータ
params = {"n1": 10, "n2": 20}

try:
    # GETリクエストを送信
    response = requests.get(url, params=params)

    # ステータスコードをチェック
    if response.status_code == 200:
        # JSONレスポンスを取得
        result = response.json()
        print("Response from server:", result)
    else:
        print("Error:", response.status_code, response.text)
except requests.exceptions.RequestException as e:
    print("Request failed:", e)
