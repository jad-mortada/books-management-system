import React, { useState, useEffect } from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  CardActions,
  Typography,
  Button,
  TextField,
  Box,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { Add as AddIcon, Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import classService from '../services/classService';

const ClassList = () => {
  const [classes, setClasses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedClass, setSelectedClass] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    gradeLevel: '',
    schoolId: '',
    teacherName: '',
    maxStudents: '',
  });

  const navigate = useNavigate();

  useEffect(() => {
    fetchClasses();
  }, []);

  const fetchClasses = async () => {
    try {
      setLoading(true);
      const response = await classService.getAllClasses();
      setClasses(response.data);
    } catch (err) {
      setError('Failed to fetch classes');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateClass = async () => {
    try {
      await classService.createClass(formData);
      setOpenDialog(false);
      setFormData({
        name: '',
        gradeLevel: '',
        schoolId: '',
        teacherName: '',
        maxStudents: '',
      });
      fetchClasses();
    } catch (err) {
      setError('Failed to create class');
    }
  };

  const handleUpdateClass = async () => {
    try {
      await classService.updateClass(selectedClass.id, formData);
      setOpenDialog(false);
      setSelectedClass(null);
      fetchClasses();
    } catch (err) {
      setError('Failed to update class');
    }
  };

  const handleDeleteClass = async (id) => {
    if (window.confirm('Are you sure you want to delete this class?')) {
      try {
        await classService.deleteClass(id);
        fetchClasses();
      } catch (err) {
        setError('Failed to delete class');
      }
    }
  };

  const openCreateDialog = () => {
    setSelectedClass(null);
    setFormData({
      name: '',
      gradeLevel: '',
      schoolId: '',
      teacherName: '',
      maxStudents: '',
    });
    setOpenDialog(true);
  };

  const openEditDialog = (classItem) => {
    setSelectedClass(classItem);
    setFormData({
      name: classItem.name,
      gradeLevel: classItem.gradeLevel,
      schoolId: classItem.schoolId,
      teacherName: classItem.teacherName,
      maxStudents: classItem.maxStudents,
    });
    setOpenDialog(true);
  };

  if (loading) return <CircularProgress />;
  if (error) return <Alert severity="error">{error}</Alert>;

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Classes Management</Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={openCreateDialog}
        >
          Add New Class
        </Button>
      </Box>

      <Box mb={3}>
        <TextField
          fullWidth
          variant="outlined"
          label="Search classes..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          sx={{ mb: 2 }}
        />
      </Box>

      <Grid container spacing={3}>
        {classes.map((classItem) => (
          <Grid item xs={12} sm={6} md={4} key={classItem.id}>
            <Card>
              <CardContent>
                <Typography variant="h6" component="h2">
                  {classItem.name}
                </Typography>
                <Typography color="text.secondary" gutterBottom>
                  Grade Level: {classItem.gradeLevel}
                </Typography>
                <Typography variant="body2">
                  Teacher: {classItem.teacherName}
                </Typography>
                <Typography variant="body2">
                  Max Students: {classItem.maxStudents}
                </Typography>
                <Typography variant="body2">
                  School ID: {classItem.schoolId}
                </Typography>
              </CardContent>
              <CardActions>
                <Button
                  size="small"
                  color="primary"
                  startIcon={<EditIcon />}
                  onClick={() => openEditDialog(classItem)}
                >
                  Edit
                </Button>
                <Button
                  size="small"
                  color="error"
                  startIcon={<DeleteIcon />}
                  onClick={() => handleDeleteClass(classItem.id)}
                >
                  Delete
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>{selectedClass ? 'Edit Class' : 'Add New Class'}</DialogTitle>
        <DialogContent>
          <Box component="form" sx={{ mt: 2 }}>
            <TextField
              fullWidth
              margin="normal"
              label="Class Name"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Grade Level"
              value={formData.gradeLevel}
              onChange={(e) => setFormData({ ...formData, gradeLevel: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="School ID"
              type="number"
              value={formData.schoolId}
              onChange={(e) => setFormData({ ...formData, schoolId: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Teacher Name"
              value={formData.teacherName}
              onChange={(e) => setFormData({ ...formData, teacherName: e.target.value })}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Max Students"
              type="number"
              value={formData.maxStudents}
              onChange={(e) => setFormData({ ...formData, maxStudents: e.target.value })}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button onClick={selectedClass ? handleUpdateClass : handleCreateClass} variant="contained">
            {selectedClass ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default ClassList;
