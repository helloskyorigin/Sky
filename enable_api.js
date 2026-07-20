const { GoogleAuth } = require('/usr/local/lib/node_modules/google-auth-library');
async function main() {
  const auth = new GoogleAuth({
    scopes: ['https://www.googleapis.com/auth/cloud-platform']
  });
  const client = await auth.getClient();
  const res = await client.request({
    url: 'https://serviceusage.googleapis.com/v1/projects/909484632641/services/firebase.googleapis.com:enable',
    method: 'POST'
  });
  console.log(res.data);
}
main().catch(console.error);
