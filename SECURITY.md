# Security

## API keys

- Never commit `app/src/main/res/raw/secrets.properties`. Use `secrets.properties.example` and keep real keys local only.
- If a Gemini/Google API key was ever pushed to GitHub (or shared elsewhere), **revoke it immediately** in [Google AI Studio](https://aistudio.google.com/apikey) or [Google Cloud Console → Credentials](https://console.cloud.google.com/apis/credentials), then create a new key for your own builds.

## History cleanup (done on maintainer clone)

Git history was rewritten with `git filter-repo` to remove `app/src/main/res/raw/secrets.properties` from all commits. That **does not** invalidate a key that was already public—you must still **rotate/revoke** that key.

## Clones and forks

After a history rewrite, **re-clone** the repository or run:

```bash
git fetch origin
git reset --hard origin/main
```

Anyone with an old fork or local clone should reset to the updated `main` (or delete the fork and fork again) so they do not reintroduce removed blobs.

## Reporting

If you find a security issue in this project, open a private GitHub advisory or contact the repository owner. Do not file public issues that include live secrets.
