# Deploying Uninex Campus Hub

Same real deployment pattern as [taskflow-api](https://github.com/nilushamadhuwanthi123/taskflow-api):
Docker-based backend on **Render**, static frontend on **GitHub Pages**. All the
config files (`Dockerfile`, `render.yaml`, the Pages workflow) are already in
this repo — the steps below are the account-side setup only you can do
(they need your own logins).

## 1. MongoDB Atlas (free cluster)

1. Sign up / log in at https://www.mongodb.com/cloud/atlas/register
2. Create a free **M0** cluster (any region).
3. Database Access -> add a database user (username + password).
4. Network Access -> add `0.0.0.0/0` (allow access from anywhere — Render's
   IPs aren't static on the free plan).
5. Connect -> Drivers -> copy the connection string, e.g.
   `mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/uninex?retryWrites=true&w=majority`
   Keep `uninex` (or any name you like) as the database name at the end.

## 2. Backend on Render

1. Log in at https://render.com (sign in with GitHub is easiest).
2. New -> Blueprint -> pick this repo. Render reads `render.yaml` at the
   repo root and proposes the `uninex-campus-hub-api` web service
   (Docker, free plan) automatically.
3. Before the first deploy, set these env vars on the service
   (Environment tab — `sync: false` in render.yaml means Render won't
   generate them, you enter them yourself):
   - `MONGO_URI` — the Atlas connection string from step 1
   - `FRONTEND_URL` — your GitHub Pages URL, i.e.
     `https://nilushamadhuwanthi123.github.io`
   - `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` — leave unset for now
     (login stays disabled until you wire OAuth2 later)
4. Deploy. Render will build the Docker image from `backend/Dockerfile`
   and give you a live URL like `https://uninex-campus-hub-api.onrender.com`
   (Render appends a random suffix if that exact name is taken — use
   whatever URL it actually gives you).
   - Free plan spins down after 15 min idle; the first request after that
     can take ~50s to wake back up — same as taskflow-api.

## 3. Frontend on GitHub Pages

1. In this repo: Settings -> Pages -> Build and deployment -> Source:
   **GitHub Actions**. (One-time toggle; the workflow does the rest.)
2. Settings -> Secrets and variables -> Actions -> **Variables** tab ->
   New repository variable:
   - Name: `API_BASE_URL`
   - Value: the Render URL from step 2 (no trailing slash), e.g.
     `https://uninex-campus-hub-api.onrender.com`
3. Push to `main` (or re-run the workflow manually from the Actions tab) —
   `.github/workflows/deploy-frontend.yml` builds the frontend with that
   API URL baked in and publishes it to
   `https://nilushamadhuwanthi123.github.io/uninex-campus-hub/`.

## 4. Wire the two together

Once both URLs are known, double-check:
- Render's `FRONTEND_URL` env var matches the real Pages URL exactly
  (scheme + host, no trailing slash) — this is what the backend's CORS
  config (`SecurityConfig.java`) allows.
- The GitHub Actions `API_BASE_URL` variable matches the real Render URL.

If either is wrong, requests will fail with a CORS error in the browser
console — update the mismatched value and redeploy/rerun that side.

## 5. Google OAuth2 login (later, optional)

Sign-in stays disabled (demo credentials) until you add real ones:

1. https://console.cloud.google.com -> APIs & Services -> Credentials ->
   Create OAuth client ID (Web application).
2. Authorized redirect URI:
   `https://<your-render-url>/login/oauth2/code/google`
3. Set `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` on Render to the real
   values and redeploy.
