import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import { api } from '@/lib/api';
import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';

const loginSchema = z.object({
  username: z.string().min(1, { message: 'Vui lòng nhập tên đăng nhập' }),
  password: z.string().min(1, { message: 'Vui lòng nhập mật khẩu' }),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export function Login() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((state) => state.setAuth);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  });

  const onSubmit = async (data: LoginFormValues) => {
    setIsLoading(true);
    setError('');
    try {
      const response = await api.post('/auth/login', data);
      
      const token = response.data?.accessToken || response.data?.token || response.data?.jwt;
      const user = response.data?.user || { id: 0, username: data.username, role: 'USER' };
      
      if (token) {
        setAuth(typeof token === 'string' ? token : token.token, user);
        navigate('/', { replace: true });
      } else {
        setError('Không nhận được token từ server.');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Tên đăng nhập hoặc mật khẩu không đúng!');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="w-full max-w-md p-8 bg-card rounded-2xl shadow-lg border animate-in fade-in zoom-in-95 duration-300">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-primary tracking-tight">Hệ thống Quản lý Bệnh nhân</h1>
        </div>

        {error && (
          <div className="mb-6 p-3 bg-destructive/10 text-destructive text-sm rounded-md border border-destructive/20 text-center">
            {error}
          </div>
        )}

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Tên đăng nhập</FormLabel>
                  <FormControl>
                    <Input placeholder="Nhập tên đăng nhập..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Mật khẩu</FormLabel>
                  <FormControl>
                    <Input type="password" placeholder="••••••••" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? 'Đang xử lý...' : 'Đăng nhập'}
            </Button>
          </form>
        </Form>
      </div>
    </div>
  );
}
