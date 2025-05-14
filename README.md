# 🖥️ Barretina DESKTOP (JavaFX)

Aplicació d'escriptori per a cuina o barra, dissenyada per gestionar i visualitzar en temps real les comandes creades pels cambrers a través de l'APP Android.

---

## ⚙️ Requisits

- Connexió a la xarxa local o al servidor WebSocket actiu
- Accés a la base de dades `barretina7` (MySQL)

---

## 🔧 Funcionalitats Principals

### 🧾 Llistat de comandes
- Visualització en temps real de les comandes entrants
- Filtratge per estat (pendent, en preparació, servida)

### 🧑‍🍳 Preparació i servei
- Permet **marcar comandes com a en preparació o servides**
- Envia actualitzacions al servidor via WebSocket perquè l’APP es mantingui sincronitzada

### 🔔 Notificacions
- Mostra alertes sonores/visuals quan entra una nova comanda

---

## 🔌 Connexió WebSocket

El client es connecta al mateix servidor WebSockets que l'APP.

---

## 🖼️ Wireframes

Els wireframes implementats per l'aplicació Desktop segueixen el Look&Feel descrit al <a href="https://github.com/AdrianCasadoAguilera/BarRetina-SRV/blob/main/README.md">repositori del servidor</a>.

![image](https://github.com/user-attachments/assets/b1e3f787-0a23-44c9-a841-e41e8abc4b28)


