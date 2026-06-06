import { Button } from "@/components/ui/button";
import { Users, UserPlus, CalendarPlus } from "lucide-react";

export function Dashboard() {
  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      <div>
        <h1 className="text-3xl font-semibold tracking-tight text-foreground">Tổng quan</h1>
        <p className="text-muted-foreground mt-2">Tình hình hoạt động của phòng khám trong ngày hôm nay.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-card p-6 rounded-xl border shadow-sm flex flex-col justify-between">
          <div className="flex items-center gap-2 text-muted-foreground mb-4">
            <Users size={18} />
            <h3 className="font-medium">Tổng Bệnh nhân</h3>
          </div>
          <p className="text-4xl font-semibold text-foreground">1,248</p>
        </div>
        
        <div className="bg-card p-6 rounded-xl border shadow-sm flex flex-col justify-between">
          <div className="flex items-center gap-2 text-primary mb-4">
            <div className="w-2 h-2 rounded-full bg-primary animate-pulse" />
            <h3 className="font-medium">Cuộc hẹn hôm nay</h3>
          </div>
          <p className="text-4xl font-semibold text-foreground">12</p>
        </div>

        <div className="bg-card p-6 rounded-xl border shadow-sm flex flex-col justify-between">
          <div className="flex items-center gap-2 text-muted-foreground mb-4">
            <h3 className="font-medium">Đơn thuốc đang dùng</h3>
          </div>
          <p className="text-4xl font-semibold text-foreground">423</p>
        </div>
      </div>

      <div className="bg-card border rounded-xl p-8 shadow-sm">
        <h3 className="text-xl font-medium mb-6">Thao tác nhanh</h3>
        <div className="flex gap-4">
          <Button className="gap-2">
            <UserPlus size={18} /> Thêm Bệnh nhân
          </Button>
          <Button variant="secondary" className="gap-2">
            <CalendarPlus size={18} /> Đặt lịch hẹn
          </Button>
        </div>
      </div>
    </div>
  );
}
