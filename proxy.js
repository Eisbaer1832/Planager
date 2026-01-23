const express = require('express')
const fetch = require('node-fetch')
const cors = require('cors')

const app = express();
const PORT = 1202;

// Allow browser access
app.use(cors());

app.get("/:schoolID/*", async (req, res) => {
    try {
        const { schoolID } = req.params;
        const { username, password } = req.query;

        if (!username || !password) {
            return res.status(400).send("Missing username or password");
        }

        // Remaining path after schoolID
        const subPath = req.params[0] || "";

        const targetUrl = `https://www.stundenplan24.de/${schoolID}/${subPath}`;

        const auth = Buffer.from(`${username}:${password}`).toString("base64");

        const upstreamResponse = await fetch(targetUrl, {
            method: "GET",
            headers: {
                "Authorization": `Basic ${auth}`,
                "User-Agent": "stundenplan24-proxy",
            },
        });

        res.status(upstreamResponse.status);
        upstreamResponse.headers.forEach((value, key) => {
            if (!["www-authenticate", "content-encoding"].includes(key.toLowerCase())) {
                res.setHeader(key, value);
            }
        });

        upstreamResponse.body.pipe(res);

    } catch (err) {
        console.error(err);
        res.status(500).send("Proxy error");
    }
});

app.listen(PORT, () => {
    console.log(`Proxy running on http://127.0.0.1:${PORT}`);
});
