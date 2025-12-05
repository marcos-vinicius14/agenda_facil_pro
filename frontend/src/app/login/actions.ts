"use server";

import { z } from "zod";
import { cookies } from "next/headers";

const statusMessages: Record<number, string> = {
    400: 'Email ou senha inválidos.',
    401: 'Email ou senha inválidos.',
    423: 'Conta bloqueada temporariamente. Tente mais tarde.',
};

const loginSchema = z.object({
    email: z.string().email("Por favor, insira um email válido."),
    password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres."),
});

export type LoginState = {
    errors?: {
        email?: string[]
        password?: string[]
        form?: string
    }
    message?: string
} | null


export async function loginState(prevState: LoginState, formData: FormData): Promise<LoginState> {
    const validationFields = loginSchema.safeParse({
        email: formData.get("email"),
        password: formData.get("password"),
    });

    if (!validationFields.success) {
        return {
            errors: validationFields.error.flatten().fieldErrors,
        };
    }

    const { email, password } = validationFields.data;
    const apiUrl = process.env.NEXT_PUBLIC_API_URL;

    try {
        const response = await fetch(`${apiUrl}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
            cache: 'no-store',
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => null)

            const message = errorData?.message || statusMessages[response.status] || 'Ocorreu um erro ao realizar o login.';

            return {
                errors: { form: message },
            }
        }

        const setCookieHeader = response.headers.get('set-cookie');

        if (setCookieHeader) {

            const parsedCookies = parseSetCookieHeader(setCookieHeader)

            const cookieStore = await cookies()

            parsedCookies.forEach(({ name, value, options }) => {
                cookieStore.set(name, value, options)
            })
        }

        return {
            message: "Login realizado com sucesso.",
        };
    } catch (error) {
        console.error('Erro de conexão:', error)
        return {
            errors: {
                form: 'Falha ao conectar com o servidor. Verifique sua conexão.',
            },
        }
    }

}

function parseSetCookieHeader(header: string | string[]) {
    const cookiesArray: any[] = []

    const parts = Array.isArray(header)
        ? header
        : header.split(/,(?=\s*[a-zA-Z0-9_-]+=)/)

    parts.forEach((part) => {
        const [first, ...rest] = part.trim().split(';')
        const [name, value] = first.split('=')

        if (!name || !value) return

        const options: any = {
            httpOnly: true,
            secure: process.env.NODE_ENV === 'production',
            path: '/',
            sameSite: 'strict',
        }

        const attributes = rest.reduce((acc, attr) => {
            const [k, v] = attr.trim().split('=')
            return { ...acc, [k.toLowerCase()]: v || true }
        }, {} as any)

        if (attributes['max-age']) {
            options.maxAge = parseInt(attributes['max-age'])
        }

        cookiesArray.push({ name: name.trim(), value: value.trim(), options })
    })

    return cookiesArray
}
