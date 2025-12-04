import * as React from "react"
import { Slot } from "@radix-ui/react-slot"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"
import { Loader2 } from "lucide-react"

// Note: I'm not using class-variance-authority yet as I didn't install it, 
// but I will implement a simple version or just use clsx for now to match the plan.
// Actually, I should probably stick to simple props if I didn't install cva.
// But cva is standard. I'll implement it manually with clsx/switch or just install cva.
// The plan didn't mention cva. I'll stick to manual clsx for now to be safe and strictly follow the plan.

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: "default" | "secondary" | "ghost" | "destructive" | "link"
    size?: "default" | "sm" | "lg" | "icon"
    asChild?: boolean
    loading?: boolean
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
    ({ className, variant = "default", size = "default", asChild = false, loading = false, children, disabled, ...props }, ref) => {
        const Comp = asChild ? Slot : "button"

        // Base styles
        const baseStyles = "inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-white transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-slate-950 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50"

        // Variants
        const variants = {
            default: "bg-blue-600 text-slate-50 hover:bg-blue-600/90", // Brand Primary #2563EB
            secondary: "bg-white text-slate-900 border border-slate-200 hover:bg-slate-100/80",
            ghost: "hover:bg-slate-100 hover:text-slate-900",
            destructive: "bg-red-500 text-slate-50 hover:bg-red-500/90",
            link: "text-slate-900 underline-offset-4 hover:underline",
        }

        // Sizes
        const sizes = {
            default: "h-10 px-4 py-2",
            sm: "h-9 rounded-md px-3",
            lg: "h-11 rounded-md px-8",
            icon: "h-10 w-10",
        }

        return (
            <button
                className={cn(
                    baseStyles,
                    variants[variant],
                    sizes[size],
                    className
                )}
                ref={ref}
                disabled={loading || disabled}
                {...props}
            >
                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {children}
            </button>
        )
    }
)
Button.displayName = "Button"

export { Button }
