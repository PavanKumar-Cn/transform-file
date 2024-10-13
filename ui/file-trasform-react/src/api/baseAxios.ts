import axios from "axios";

const BASE_URl = "http://localhost:8092/api";

export const openaxios = axios.create({
  baseURL: BASE_URl,
});
