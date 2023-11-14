import axios from 'axios'

export default class ApplicationRestResource {

  constructor() {
    this.applicationAxios = axios.create({
      baseURL: `${window.location.protocol}//${window.location.hostname}:${window.location.port}`,
      headers: {
        "Content-Type": "text/plain;charset=UTF-8",
        "Access-Control-Allow-Origin": "*"
      }
    });
  }

  getVersion() {
    return this.applicationAxios.get("/version/", {
      transformResponse: x => x,
    }).then(response => response.data);
  }

  getMotd() {
    return this.applicationAxios.get("/motd/", {
      transformResponse: x => x,
    }).then(response => response.data);
  }

  getIsDemo() {
    return this.applicationAxios.get("/demo/", {
      transformResponse: x => x == "true",
    }).then(response => response.data);
  }
}