import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { format, parseISO } from 'date-fns';
import { reportApi, getTodayAppointments, getPatientStats } from '@/lib/reportApi';
import { Loader2, Users, Calendar, Pill, TrendingUp, AlertTriangle, Activity } from 'lucide-react';
import { Button } from '@/components/ui/button';

function StatCard({
  icon,
  label,
  value,
  sub,
  color,
  onClick,
}: {
  icon: React.ReactNode;
  label: string;
  value: string | number;
  sub?: string;
  color: string;
  onClick?: () => void;
}) {
  return (
    <div
      onClick={onClick}
      className={`bg-card border rounded-2xl p-5 shadow-sm flex flex-col gap-3 ${onClick ? 'cursor-pointer hover:shadow-md transition-shadow' : ''}`}
    >
      <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${color}`}>
        {icon}
      </div>
      <div>
        <p className="text-sm text-muted-foreground">{label}</p>
        <p className="text-3xl font-bold tracking-tight mt-0.5">{value}</p>
        {sub && <p className="text-xs text-muted-foreground mt-1">{sub}</p>}
      </div>
    </div>
  );
}

function BpRateCard() {
  const { data, isLoading } = useQuery({
    queryKey: ['report-bp-rate'],
    queryFn: reportApi.getHighBpRate,
  });

  const pct = data?.percentage ?? 0;
  const total = data?.total ?? 0;
  const highCount = data?.highBpCount ?? 0;

  return (
    <div className="bg-card border rounded-2xl p-5 shadow-sm flex flex-col gap-4">
      <div className="flex items-center gap-2">
        <div className="w-10 h-10 rounded-xl bg-red-100 flex items-center justify-center">
          <AlertTriangle size={18} className="text-red-600" />
        </div>
        <div>
          <p className="font-semibold text-sm">Tỉ lệ huyết áp cao</p>
          <p className="text-xs text-muted-foreground">{highCount} / {total} bệnh nhân đã đo</p>
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-4"><Loader2 size={20} className="animate-spin text-muted-foreground" /></div>
      ) : (
        <>
          {/* Progress bar */}
          <div>
            <div className="flex justify-between text-xs text-muted-foreground mb-1.5">
              <span>Bình thường</span>
              <span className="font-semibold text-red-600">{Number(pct).toFixed(1)}% cao</span>
            </div>
            <div className="h-3 rounded-full bg-muted overflow-hidden">
              <div
                className="h-full rounded-full bg-linear-to-r from-amber-400 to-red-500 transition-all duration-700"
                style={{ width: `${Math.min(pct, 100)}%` }}
              />
            </div>
          </div>

          {/* Risk level */}
          <div className={`text-xs font-medium px-3 py-1.5 rounded-lg w-fit ${
            pct >= 30 ? 'bg-red-50 text-red-700' : pct >= 15 ? 'bg-amber-50 text-amber-700' : 'bg-emerald-50 text-emerald-700'
          }`}>
            {pct >= 30 ? '⚠ Mức cao — cần chú ý' : pct >= 15 ? '⚡ Mức trung bình' : '✓ Mức an toàn'}
          </div>
        </>
      )}
    </div>
  );
}

function TopBmiTable() {
  const { data = [], isLoading } = useQuery({
    queryKey: ['report-top-bmi'],
    queryFn: () => reportApi.getTopBmi(8),
  });

  const bmiColor = (bmi: number) => {
    if (bmi >= 30) return 'text-red-600 font-semibold';
    if (bmi >= 25) return 'text-orange-500 font-medium';
    return 'text-foreground';
  };

  return (
    <div className="bg-card border rounded-2xl shadow-sm overflow-hidden">
      <div className="px-5 py-4 border-b flex items-center gap-2">
        <TrendingUp size={16} className="text-primary" />
        <h3 className="font-semibold text-sm">Bệnh nhân BMI cao nhất</h3>
      </div>
      {isLoading ? (
        <div className="flex justify-center py-10"><Loader2 size={20} className="animate-spin text-muted-foreground" /></div>
      ) : data.length === 0 ? (
        <div className="text-center py-10 text-muted-foreground text-sm">Chưa có dữ liệu</div>
      ) : (
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-muted/40">
              <th className="text-left px-5 py-2.5 text-xs font-medium text-muted-foreground">#</th>
              <th className="text-left px-4 py-2.5 text-xs font-medium text-muted-foreground">Bệnh nhân</th>
              <th className="text-center px-4 py-2.5 text-xs font-medium text-muted-foreground">CN (kg)</th>
              <th className="text-center px-4 py-2.5 text-xs font-medium text-muted-foreground">CC (cm)</th>
              <th className="text-center px-4 py-2.5 text-xs font-medium text-muted-foreground">BMI</th>
              <th className="text-right px-5 py-2.5 text-xs font-medium text-muted-foreground">Ngày đo</th>
            </tr>
          </thead>
          <tbody>
            {data.map((p, i) => (
              <tr key={p.patientCode} className="border-t hover:bg-muted/20 transition-colors">
                <td className="px-5 py-3 text-muted-foreground font-mono text-xs">{i + 1}</td>
                <td className="px-4 py-3">
                  <p className="font-medium">{p.fullName}</p>
                  <p className="text-xs text-muted-foreground font-mono">{p.patientCode}</p>
                </td>
                <td className="px-4 py-3 text-center">{p.weight}</td>
                <td className="px-4 py-3 text-center">{p.height}</td>
                <td className={`px-4 py-3 text-center ${bmiColor(p.bmi)}`}>
                  {Number(p.bmi).toFixed(1)}
                </td>
                <td className="px-5 py-3 text-right text-xs text-muted-foreground">
                  {p.measuredAt ? format(parseISO(p.measuredAt), 'dd/MM/yyyy') : '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function ActiveMedicationsCard() {
  const { data = [], isLoading } = useQuery({
    queryKey: ['report-active-meds'],
    queryFn: reportApi.getActiveMedications,
  });

  const maxVal = Math.max(...data.map((d) => Number(d.totalPrescription ?? 0)), 1);

  return (
    <div className="bg-card border rounded-2xl shadow-sm overflow-hidden">
      <div className="px-5 py-4 border-b flex items-center gap-2">
        <Activity size={16} className="text-primary" />
        <h3 className="font-semibold text-sm">Thuốc được dùng nhiều nhất</h3>
      </div>
      {isLoading ? (
        <div className="flex justify-center py-10"><Loader2 size={20} className="animate-spin text-muted-foreground" /></div>
      ) : data.length === 0 ? (
        <div className="text-center py-10 text-muted-foreground text-sm">Chưa có dữ liệu</div>
      ) : (
        <div className="px-5 py-4 space-y-3">
          {data.slice(0, 8).map((med, i) => {
            const name = String(med.medicationName ?? med.medication_name ?? `Thuốc ${i + 1}`);
            const count = Number(med.totalPrescription ?? med.total_prescription ?? 0);
            const pct = (count / maxVal) * 100;
            return (
              <div key={i} className="space-y-1">
                <div className="flex justify-between text-sm">
                  <span className="font-medium truncate max-w-[70%]">{name}</span>
                  <span className="text-muted-foreground text-xs">{count} đơn</span>
                </div>
                <div className="h-2 rounded-full bg-muted overflow-hidden">
                  <div
                    className="h-full rounded-full bg-primary/70 transition-all duration-500"
                    style={{ width: `${pct}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export function Dashboard() {
  const navigate = useNavigate();

  const { data: patientStats } = useQuery({
    queryKey: ['patient-stats'],
    queryFn: getPatientStats,
  });

  const { data: todayAppointments = [] } = useQuery({
    queryKey: ['today-appointments'],
    queryFn: getTodayAppointments,
  });

  const { data: activeMeds = [] } = useQuery({
    queryKey: ['report-active-meds-count'],
    queryFn: reportApi.getActiveMedications,
  });

  const today = format(new Date(), "EEEE, dd/MM/yyyy");

  return (
    <div className="flex flex-col gap-6 animate-in fade-in-0">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Tổng quan</h1>
          <p className="text-muted-foreground text-sm mt-0.5 capitalize">{today}</p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={() => navigate('/patients')}>
            + Thêm bệnh nhân
          </Button>
          <Button size="sm" onClick={() => navigate('/appointments')}>
            + Đặt lịch hẹn
          </Button>
        </div>
      </div>

      {/* Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <StatCard
          icon={<Users size={18} className="text-blue-600" />}
          label="Tổng bệnh nhân"
          value={patientStats?.totalElements?.toLocaleString('vi-VN') ?? '—'}
          sub="Trong hệ thống"
          color="bg-blue-100"
          onClick={() => navigate('/patients')}
        />
        <StatCard
          icon={<Calendar size={18} className="text-emerald-600" />}
          label="Lịch hẹn hôm nay"
          value={(todayAppointments as unknown[]).length}
          sub={`${(todayAppointments as { status: string }[]).filter((a) => a.status === 'SCHEDULED').length} chờ khám`}
          color="bg-emerald-100"
          onClick={() => navigate('/appointments')}
        />
        <StatCard
          icon={<Pill size={18} className="text-violet-600" />}
          label="Loại thuốc đang dùng"
          value={activeMeds.length}
          sub="Trong các đơn thuốc"
          color="bg-violet-100"
          onClick={() => navigate('/medications')}
        />
      </div>

      {/* Analytics Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <BpRateCard />
        <div className="lg:col-span-2">
          <ActiveMedicationsCard />
        </div>
      </div>

      {/* Top BMI Table */}
      <TopBmiTable />
    </div>
  );
}
