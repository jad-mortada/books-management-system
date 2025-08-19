import api from './api';

const schoolService = {
  getAllSchools: () => api.get('/schools'),
  getSchoolById: (id) => api.get(`/schools/${id}`),
  createSchool: (schoolData) => api.post('/schools', schoolData),
  updateSchool: (id, schoolData) => api.put(`/schools/${id}`, schoolData),
  deleteSchool: (id) => api.delete(`/schools/${id}`),
};

export default schoolService;
