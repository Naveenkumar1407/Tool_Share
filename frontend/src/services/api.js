import axios from 'axios';

const USER_API = 'http://localhost:8081/api';
const TOOL_API = 'http://localhost:8082/api';

// helper to get auth header
function authHeader() {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

// ===================== AUTH =====================
export async function registerUser(data) {
  const res = await axios.post(`${USER_API}/auth/register`, data);
  return res.data;
}

export async function loginUser(data) {
  const res = await axios.post(`${USER_API}/auth/login`, data);
  return res.data;
}

// ===================== USER =====================
export async function getMyProfile() {
  const res = await axios.get(`${USER_API}/users/me`, { headers: authHeader() });
  return res.data;
}

export async function updateProfile(data) {
  const res = await axios.put(`${USER_API}/users/me`, data, { headers: authHeader() });
  return res.data;
}

export async function getUserById(id) {
  const res = await axios.get(`${USER_API}/users/${id}`);
  return res.data;
}

// ===================== TOOLS =====================
export async function getTools(search, category) {
  let url = `${TOOL_API}/tools`;
  const params = [];
  if (search) params.push(`search=${encodeURIComponent(search)}`);
  if (category) params.push(`category=${encodeURIComponent(category)}`);
  if (params.length) url += '?' + params.join('&');
  const res = await axios.get(url);
  return res.data;
}

export async function getToolById(id) {
  const res = await axios.get(`${TOOL_API}/tools/${id}`);
  return res.data;
}

export async function getMyTools() {
  const res = await axios.get(`${TOOL_API}/tools/my`, { headers: authHeader() });
  return res.data;
}

export async function addTool(data) {
  const res = await axios.post(`${TOOL_API}/tools`, data, { headers: authHeader() });
  return res.data;
}

export async function updateTool(id, data) {
  const res = await axios.put(`${TOOL_API}/tools/${id}`, data, { headers: authHeader() });
  return res.data;
}

export async function deleteTool(id) {
  const res = await axios.delete(`${TOOL_API}/tools/${id}`, { headers: authHeader() });
  return res.data;
}

// ===================== BORROWS =====================
export async function createBorrowRequest(data) {
  const res = await axios.post(`${TOOL_API}/borrows`, data, { headers: authHeader() });
  return res.data;
}

export async function getMyBorrowRequests() {
  const res = await axios.get(`${TOOL_API}/borrows/my-requests`, { headers: authHeader() });
  return res.data;
}

export async function getIncomingRequests() {
  const res = await axios.get(`${TOOL_API}/borrows/incoming`, { headers: authHeader() });
  return res.data;
}

export async function approveBorrow(id) {
  const res = await axios.put(`${TOOL_API}/borrows/${id}/approve`, {}, { headers: authHeader() });
  return res.data;
}

export async function rejectBorrow(id) {
  const res = await axios.put(`${TOOL_API}/borrows/${id}/reject`, {}, { headers: authHeader() });
  return res.data;
}

export async function markReturned(id) {
  const res = await axios.put(`${TOOL_API}/borrows/${id}/return`, {}, { headers: authHeader() });
  return res.data;
}
