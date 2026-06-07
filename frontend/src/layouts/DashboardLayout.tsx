import { Outlet, Link, useLocation } from 'react-router-dom';
import { Users, Calendar, Pill, LayoutDashboard, Activity, LogOut } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { Button } from '@/components/ui/button';

export function DashboardLayout() {
  const logout = useAuthStore((state) => state.logout);
  const location = useLocation();

  const getLinkClass = (path: string) => {
    const isActive = location.pathname === path;
    return `flex items-center gap-3 px-3 py-2 rounded-md transition-colors ${
      isActive 
        ? 'bg-primary/10 text-primary font-medium' 
        : 'text-muted-foreground hover:text-foreground hover:bg-muted'
    }`;
  };

  return (
    <div className="flex h-screen bg-background text-foreground">
      {/* Sidebar */}
      <aside className="w-64 bg-card border-r flex flex-col">
        
        <nav className="flex-1 px-4 space-y-1 mt-4">
          <Link to="/" className={getLinkClass('/')}>
            <LayoutDashboard size={20} /> Bảng điều khiển
          </Link>
          <Link to="/patients" className={getLinkClass('/patients')}>
            <Users size={20} /> Bệnh nhân
          </Link>
          <Link to="/appointments" className={getLinkClass('/appointments')}>
            <Calendar size={20} /> Cuộc hẹn
          </Link>
          <Link to="/medications" className={getLinkClass('/medications')}>
            <Pill size={20} /> Thuốc & Kê đơn
          </Link>
          <Link to="/metrics" className={getLinkClass('/metrics')}>
            <Activity size={20} /> Chỉ số sức khỏe
          </Link>
        </nav>

        <div className="p-4 border-t">
          <Button variant="ghost" className="w-full justify-start text-muted-foreground hover:text-destructive" onClick={logout}>
            <LogOut size={20} className="mr-2" /> Đăng xuất
          </Button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">    
        <div className="flex-1 overflow-auto p-8 bg-background">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
