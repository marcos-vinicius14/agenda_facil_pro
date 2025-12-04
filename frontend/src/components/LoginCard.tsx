"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useState, useTransition } from "react";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Card } from "@/components/ui/Card";
import { Eye, EyeOff, Stethoscope } from "lucide-react";
import Link from "next/link";
import { cn } from "@/lib/utils";
import { login } from "@/app/login/actions";

const loginSchema = z.object({
    email: z.string().min(1, "O campo é obrigatório"),
    password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres."),
});

type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginCard() {
    const [isPending, startTransition] = useTransition();
    const [serverError, setServerError] = useState<string | null>(null);
    const [serverSuccess, setServerSuccess] = useState<boolean>(false);
    const [showPassword, setShowPassword] = useState(false);

    const [patientType, setPatientType] = useState<"new" | "existing">("existing");

    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginFormValues>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    });

    const onSubmit = (data: LoginFormValues) => {
        setServerError(null);
        setServerSuccess(false);

        startTransition(async () => {
            const formData = new FormData();
            formData.append("email", data.email);
            formData.append("password", data.password);

            const result = await login({} as any, formData);

            if (result.success) {
                setServerSuccess(true);
            } else {
                setServerError(result.message || "Aconteceu um erro inesperado.");
            }
        });
    };

    return (
        <Card className="w-full max-w-[900px] overflow-hidden rounded-3xl border-none shadow-[0_20px_40px_rgba(0,0,0,0.08)] bg-white my-auto">
            <div className="grid lg:grid-cols-2 min-h-[500px]">

                <div className="rounded-3xl relative hidden lg:flex flex-col justify-center items-center text-center bg-[#F8FAFC] p-8 overflow-hidden">


                    <div className="relative z-30 flex flex-col items-center max-w-md">
                        <div className="mb-8 bg-white p-5 shadow-xl shadow-blue-100/50 ring-1 ring-blue-50 animate-in zoom-in duration-700">
                            <Stethoscope className="h-20 w-20 text-blue-600" strokeWidth={1.5} />
                        </div>

                        <h2 className="font-heading text-3xl font-extrabold leading-tight text-slate-900 mb-5 tracking-tight">
                            Simplifique a <span className="bg-linear-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">Gestão da sua Clínica.</span>
                        </h2>

                        <p className="text-lg text-slate-600 font-medium leading-relaxed">
                            Organize agendamentos, reduza faltas e ofereça a melhor experiência para seus pacientes com nossa plataforma inteligente.
                        </p>
                    </div>




                    <div className="absolute top-40 right-12 text-blue-400/60 text-4xl font-black z-10 animate-pulse">✦</div>
                    <div className="absolute top-20 left-12 text-blue-600/40 text-2xl font-black z-10">✷</div>
                </div>

                <div className="flex flex-col justify-center bg-white px-8 py-8 lg:px-12">
                    <div className="mb-8 flex rounded-full bg-slate-100 p-1 w-full max-w-sm">
                        <button
                            type="button"
                            onClick={() => setPatientType("new")}
                            className={cn(
                                "flex-1 rounded-full py-2 text-sm font-semibold transition-all duration-200",
                                patientType === "new"
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "text-slate-500 hover:text-slate-700"
                            )}
                        >
                            Criar Conta
                        </button>
                        <button
                            type="button"
                            onClick={() => setPatientType("existing")}
                            className={cn(
                                "flex-1 rounded-full py-2 text-sm font-semibold transition-all duration-200",
                                patientType === "existing"
                                    ? "bg-blue-600 text-white shadow-md"
                                    : "text-slate-500 hover:text-slate-700"
                            )}
                        >
                            Login
                        </button>
                    </div>



                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                        <div className="space-y-1.5">
                            <Label htmlFor="email" className="text-slate-600 font-medium text-xs">Email</Label>
                            <div className="relative group">
                                <Input
                                    id="email"
                                    type="text"
                                    placeholder="email@example.com"
                                    {...register("email")}
                                    className={cn(
                                        "h-12 rounded-xl bg-slate-50 border-slate-200 text-base font-medium transition-all",
                                        "focus-visible:ring-blue-600 focus-visible:ring-offset-0 focus-visible:border-blue-600",
                                        errors.email ? "border-red-500 bg-red-50" : ""
                                    )}
                                />
                            </div>
                            {errors.email && (
                                <p className="text-sm text-red-500 ml-1">{errors.email.message}</p>
                            )}
                        </div>

                        <div className="space-y-1.5">
                            <Label htmlFor="password" className="text-slate-600 font-medium text-xs">Password</Label>
                            <div className="relative">
                                <Input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="•••••••••"
                                    {...register("password")}
                                    className={cn(
                                        "h-12 rounded-xl bg-slate-50 border-slate-200 text-base font-medium transition-all",
                                        "focus-visible:ring-blue-600 focus-visible:ring-offset-0 focus-visible:border-blue-600",
                                        errors.password ? "border-red-500 bg-red-50" : ""
                                    )}
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 focus:outline-none"
                                >
                                    {showPassword ? (
                                        <EyeOff className="h-5 w-5" />
                                    ) : (
                                        <Eye className="h-5 w-5" />
                                    )}
                                </button>
                            </div>
                            <div className="flex justify-end">
                                <Link
                                    href="/forgot-password"
                                    className="text-xs font-semibold text-slate-500 underline decoration-slate-300 underline-offset-4 hover:text-blue-600 hover:decoration-blue-600 transition-colors"
                                >
                                    Recuperar Senha
                                </Link>
                            </div>
                        </div>

                        {serverError && (
                            <div className="rounded-xl bg-red-50 p-3 text-sm font-medium text-red-600 border border-red-100">
                                {serverError}
                            </div>
                        )}
                        {serverSuccess && (
                            <div className="rounded-xl bg-green-50 p-3 text-sm font-medium text-green-600 border border-green-100">
                                Login realizado com sucesso!
                            </div>
                        )}

                        <div className="pt-1 space-y-4">
                            <Button
                                type="submit"
                                className="w-full h-12 rounded-xl bg-blue-600 text-base font-bold hover:bg-blue-700 shadow-lg shadow-blue-200 transition-transform active:scale-[0.98]"
                                loading={isPending}
                            >
                                Login
                            </Button>
                        </div>
                    </form>
                </div>
            </div>
        </Card>
    );
}