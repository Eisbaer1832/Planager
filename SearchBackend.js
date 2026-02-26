const express = require('express')
const app = express()
const path = require('path');
const PORT = 7788;
const basicAuth = require("express-basic-auth");

app.use(
    basicAuth({
	users: { "planager" : "CHANGE THIS PASSWORD" },
        challenge: true,
        unauthorizedResponse: "Unauthorized access. Please provide valid credentials.",
    })
);


app.get('/', function (req, res) {
	const options = {root: path.join(__dirname)};
	res.sendFile("./PlanKl" + req.query.date + ".xml", options, function (err) {

	if (err) console.log(err + " for " + req.query.date + " ")
    });
});

app.listen(PORT, function (err) {
    if (err) console.error(err);
    console.log("Server listening on PORT", PORT);
});
