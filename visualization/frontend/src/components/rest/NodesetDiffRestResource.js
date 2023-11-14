import axios from 'axios'

export default class NodesetDiffRestResource {

  constructor() {
    const port = window.location.port;
    this.baseURL = `${window.location.protocol}//${window.location.hostname}:${port}`;
    this.diffAxios = axios.create({
      baseURL: this.baseURL,
      headers: {
        "Content-Type": "text/plain;charset=UTF-8",
        "Access-Control-Allow-Origin": "*"
      }
    });
  }

  generateDiff(oldPath, newPath) {
    const params = new FormData();
    params.append('base', oldPath);
    params.append('compare', newPath);
    return this.diffAxios.post("/generateDiff/", params);
  }

  loadDiffs() {
    return this.diffAxios.get("/diffs/");
  }

  deleteDiff(relativePath) {
    return this.diffAxios.delete("/diff/", { params: { path: relativePath } });
  }

  getDiff(relativePath) {
    window.open(`${this.baseURL}/diffs/${relativePath}`);
  }

}
