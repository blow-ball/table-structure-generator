var request = {
    // 封装发送请求的方法
    request: function (url, method, data, contentType) {
        return $.ajax({
            url: url,
            type: method,
            data: data,
            contentType: contentType,
        });
    },

    // 封装发送普通参数的 GET 请求
    get: function (url, data) {
        return this.request(url, "GET", data, "application/x-www-form-urlencoded");
    },

    // 封装发送 JSON 参数的 POST 请求
    postJson: function (url, data) {
        return this.request(url, "POST", JSON.stringify(data), "application/json");
    },

    // 封装发送普通参数的 POST 请求
    post: function (url, data) {
        return this.request(url, "POST", data, "application/x-www-form-urlencoded");
    },

    // 封装发送 JSON 参数的 PUT 请求
    putJson: function (url, data) {
        return this.request(url, "PUT", JSON.stringify(data), "application/json");
    },

    // 封装 DELETE 请求
    delete: function (url) {
        return this.request(url, "DELETE", null, null);
    },
};