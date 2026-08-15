import requests
import pytest

BASE_URL = "http://localhost:8080/api/accounts"

def test_deposit():
    requests.post(f"{BASE_URL}/reset")
    resp = requests.post(f"{BASE_URL}/deposit?amount=100")
    assert resp.status_code == 200
    assert "存款成功，当前余额：100" in resp.text

    balance_resp = requests.get(f"{BASE_URL}/balance")
    assert "当前余额：100" in balance_resp.text