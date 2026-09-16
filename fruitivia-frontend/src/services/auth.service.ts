import api from "./api"
import { z } from "zod"

export const loginSchema = z.object({
  username: z.string().min(1, "Username is required"),
  password: z.string().min(1, "Password is required"),
})

export type LoginRequest = z.infer<typeof loginSchema>

export interface AuthResponse {
  token: string
}

export const authService = {
  login: async (data: LoginRequest) => {
    const response = await api.post<AuthResponse>("/v1/auth/login", data)
    return response.data
  },
  
  // Registration and other auth endpoints can be added here
}
