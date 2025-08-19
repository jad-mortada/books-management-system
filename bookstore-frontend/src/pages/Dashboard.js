import React from 'react';
import {
  Container,
  Grid,
  Card,
  CardContent,
  Typography,
  Box
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import {
  School as SchoolIcon,
  Class as ClassIcon,
  Book as BookIcon,
  Person as PersonIcon
} from '@mui/icons-material';

const Dashboard = () => {
  const navigate = useNavigate();

  const menuItems = [
    {
      title: 'Schools',
      description: 'Manage schools',
      icon: <SchoolIcon sx={{ fontSize: 40 }} />,
      path: '/schools',
      color: '#1976d2'
    },
    {
      title: 'Classes',
      description: 'Manage classes',
      icon: <ClassIcon sx={{ fontSize: 40 }} />,
      path: '/classes',
      color: '#388e3c'
    },
    {
      title: 'Books',
      description: 'Manage books',
      icon: <BookIcon sx={{ fontSize: 40 }} />,
      path: '/books',
      color: '#f57c00'
    },
    {
      title: 'Customers',
      description: 'Manage customers',
      icon: <PersonIcon sx={{ fontSize: 40 }} />,
      path: '/customers',
      color: '#7b1fa2'
    }
  ];

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h3" component="h1" gutterBottom>
        Dashboard
      </Typography>
      
      <Grid container spacing={3}>
        {menuItems.map((item) => (
          <Grid item xs={12} sm={6} md={3} key={item.title}>
            <Card 
              sx={{ 
                height: '100%', 
                cursor: 'pointer',
                '&:hover': { transform: 'scale(1.02)', transition: 'transform 0.2s' }
              }}
              onClick={() => navigate(item.path)}
            >
              <CardContent sx={{ textAlign: 'center' }}>
                <Box sx={{ color: item.color, mb: 2 }}>
                  {item.icon}
                </Box>
                <Typography variant="h6" component="h2" gutterBottom>
                  {item.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {item.description}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
};

export default Dashboard; 