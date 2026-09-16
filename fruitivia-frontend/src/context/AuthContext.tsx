import { createContext, useContext, useState, useEffect } from "react"
import type { ReactNode } from "react"
import { jwtDecode } from "jwt-decode"
import api from "../services/api"

interface JwtPayload {
  sub: string
  roles: string[]
  exp: number
}

interface AuthContextType {
  token: string | null
  roles: string[]
  isAuthenticated: boolean
  isLoading: boolean
  login: (token: string) => void
  logout: () => void
  hasRole: (role: string) => boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null)
  const [roles, setRoles] = useState<string[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const storedToken = localStorage.getItem("token")
    if (storedToken) {
      try {
        const decoded = jwtDecode<JwtPayload>(storedToken)
        if (decoded.exp * 1000 > Date.now()) {
          setToken(storedToken)
          setRoles(decoded.roles || [])
        } else {
          // Token expired
          localStorage.removeItem("token")
        }
      } catch (e) {
        localStorage.removeItem("token")
      }
    }
    setIsLoading(false)
  }, [])

  // Hook into axios to handle 401s
  useEffect(() => {
    const interceptor = api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          logout()
        }
        return Promise.reject(error)
      }
    )

    return () => {
      api.interceptors.response.eject(interceptor)
    }
  }, [])

  const login = (newToken: string) => {
    localStorage.setItem("token", newToken)
    const decoded = jwtDecode<JwtPayload>(newToken)
    setToken(newToken)
    setRoles(decoded.roles || [])
  }

  const logout = () => {
    localStorage.removeItem("token")
    setToken(null)
    setRoles([])
    // Redirect handled by ProtectedRoute or can force reload
    window.location.href = '/login'
  }

  const hasRole = (role: string) => {
    return roles.includes(`ROLE_${role}`) || roles.includes(role)
  }

  return (
    <AuthContext.Provider
      value={{
        token,
        roles,
        isAuthenticated: !!token,
        isLoading,
        login,
        logout,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
