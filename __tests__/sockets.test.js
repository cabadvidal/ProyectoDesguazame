const { io } = require("socket.io-client");

for (let i = 1; i <= 50; i++) {
    describe(`🔁 Iteración de pruebas socket #${i}`, () => {
        let socket;
        let token;

        beforeAll((done) => {
            socket = io("http://83.37.180.236:10010", {
                transports: ["websocket"],
                reconnection: false,
            });

            socket.on("connect", () => {
                const datos = {
                    usuario: "cliente@cliente.com",
                    password: "Cliente123@",
                    tokenFcm: "dummyTokenFCM"
                };
                socket.emit("comprobar credenciales", datos);
                socket.once("token_cliente", (res) => {
                    token = res.token;
                    expect(token).toBeDefined();
                    done();
                });
            });
        });

        afterAll(() => {
            if (socket && socket.connected) {
                socket.disconnect();
            }
        });

        test("Reconexión con token válido", (done) => {
            socket.emit("reconexion", token);
            socket.once("respuesta verificación", (res) => {
                expect(res.valido).toBe(true);
                done();
            });
        });

        test("Emitir actividad válida", (done) => {
            socket.emit("actividad", {
                token: token,
                actividad: "consultar piezas"
            });
            socket.once("respuesta actividad", (res) => {
                expect(res.valido).toBe(true);
                done();
            });
        });

        test("Desconexión de usuario", (done) => {
            socket.emit("desconectar", token);
            done();
        });
    });
}