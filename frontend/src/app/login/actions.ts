"use server";

import { z } from "zod";

const loginSchema = z.object({
    email: z.string().email("Por favor, insira um email válido."),
    password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres."),
});

export type LoginState = {
    errors?: {
        email?: string[];
        password?: string[];
    };
    message?: string;
    success?: boolean;
};

export async function login(prevState: LoginState, formData: FormData): Promise<LoginState> {
    await new Promise((resolve) => setTimeout(resolve, 1500));

    const validatedFields = loginSchema.safeParse({
        email: formData.get("email"),
        password: formData.get("password"),
    });

    if (!validatedFields.success) {
        return {
            errors: validatedFields.error.flatten().fieldErrors,
            message: "Preencha todos os campos corretamente.",
            success: false,
        };
    }

    const { email, password } = validatedFields.data;

    // Simulate authentication logic
    if (email === "test@example.com" && password === "password") {
        return {
            success: true,
            message: "Login successful!",
        };
    }

    return {
        success: true,
        message: "Login successful!",
    };
}
