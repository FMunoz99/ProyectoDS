import Dashboard from './Pages/Dashboard.tsx';
import Login from './Pages/Login.tsx';
import Register from './Pages/Register.tsx';
import Team from './Pages/Team.tsx';
import UserProfile_Overview from './Pages/Profile-Overview.tsx';
import UserProfile_EditProfile from './Pages/Profile-EditProfile.tsx';
import UserProfile_Setting from './Pages/Profile-Setting.tsx';
import UserProfile_ChangePassword from './Pages/Profile-ChangePassword.tsx';
import Reports from './Pages/Reports.tsx';
import Create_Report from './Pages/Create_Report.tsx';
import TasksEmployee from './Pages/TasksEmployee.tsx';
import User from './Pages/Users_Admin.tsx';
import Report_Admin from './Pages/Reports_Admin.tsx';
import CreateAdmin from './Pages/Create_Admin.tsx';
import CreateEmployee from './Pages/Create_Employee.tsx';
import CreateUser from './Pages/Create_User.tsx';
import ViewReportsEmp from './Pages/View_User_Emp.tsx';
import ViewReportsEst from './Pages/View_User_Est.tsx';
import View_Inc from './Pages/View_Inc_Admin.tsx';
import View_Obj from './Pages/View_Obj_Admin.tsx';
import EditUser from './Pages/Edit_User.tsx';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} /> 
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/register" element={<Register />} />
        <Route path="/team" element={<Team />} />
        <Route path="/profile" element={<UserProfile_Overview />} />
        <Route path="/profile/overview" element={<UserProfile_Overview />} />
        <Route path="/profile/edit" element={<UserProfile_EditProfile />} />
        <Route path="/profile/settings" element={<UserProfile_Setting />} />
        <Route path="/profile/change-password" element={<UserProfile_ChangePassword />} />
        <Route path="/reports" element={<Reports />} />
        <Route path="/reports/create-report" element={<Create_Report />} />
        <Route path="/tasksEmployee" element={<TasksEmployee />} />
        <Route path="/users" element={<User />} />
        <Route path="/all-reports" element={<Report_Admin />} />
        <Route path="/users/create-user" element={<CreateUser />} />
        <Route path="/users/create-admin" element={<CreateAdmin />} />
        <Route path="/users/reports/Estudiante/:id" element={<ViewReportsEst />} />
        <Route path="/users/reports/Empleado/:id" element={<ViewReportsEmp />} />
        <Route path="/users/edit/:id" element={<EditUser />} />
        <Route path="/users/create-employee" element={<CreateEmployee />} />
        <Route path="/lost-objects/:id" element={<View_Obj />} />
        <Route path="/incidents/:id" element={<View_Inc />} />
        <Route path="/incidencias" element={<View_Inc />} />
      </Routes>
    </Router>
  );
};

export default App;
