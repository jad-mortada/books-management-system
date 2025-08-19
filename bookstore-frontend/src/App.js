import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navigation from './components/Navigation';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import BookList from './pages/BookList';
import SchoolList from './pages/SchoolList';
import CustomerList from './pages/CustomerList';
import ClassList from './pages/ClassList';
import CustomerBookOrderList from './pages/CustomerBookOrderList';
import { Box, CircularProgress } from '@mui/material';
import './App.css';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  
  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }
  
  return user ? (
    <>
      <Navigation />
      {children}
    </>
  ) : <Navigate to="/login" />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route 
              path="/dashboard" 
              element={
                <PrivateRoute>
                  <Dashboard />
                </PrivateRoute>
              } 
            />
            <Route path="/" element={<Navigate to="/dashboard" />} />
            <Route 
              path="/books" 
              element={
                <PrivateRoute>
                  <BookList />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/schools" 
              element={
                <PrivateRoute>
                  <SchoolList />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/customers" 
              element={
                <PrivateRoute>
                  <CustomerList />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/classes" 
              element={
                <PrivateRoute>
                  <ClassList />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/orders" 
              element={
                <PrivateRoute>
                  <CustomerBookOrderList />
                </PrivateRoute>
              } 
            />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
