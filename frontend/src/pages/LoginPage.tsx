import { type FormEvent, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import { getApiErrorMessage } from "../api/errors";
import { AuthField, AuthLayout } from "../components/auth/AuthLayout";
import { useAuthStore } from "../stores/authStore";

export default function LoginPage() {
  const accessToken = useAuthStore((state) => state.accessToken);
  const setSession = useAuthStore((state) => state.setSession);
  const navigate = useNavigate();
  const [email, setEmail] = useState("ada@exacta.test");
  const [password, setPassword] = useState("ExactaDemo1!");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (accessToken) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      const response = await login({ email, password });
      setSession(response.accessToken, response.user);
      navigate("/", { replace: true });
    } catch (err) {
      setError(getApiErrorMessage(err, "Could not sign in"));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout
      title="Sign in"
      subtitle="Use the demo admin account, or register a new firm user."
      submitLabel="Sign in"
      isSubmitting={isSubmitting}
      error={error}
      onSubmit={(event) => {
        void handleSubmit(event);
      }}
      footer={
        <>
          New here?{" "}
          <Link to="/register" className="text-blue-300 hover:text-blue-200">
            Create an account
          </Link>
        </>
      }
    >
      <AuthField
        id="email"
        label="Email"
        type="email"
        value={email}
        autoComplete="email"
        onChange={setEmail}
      />
      <AuthField
        id="password"
        label="Password"
        type="password"
        value={password}
        autoComplete="current-password"
        minLength={8}
        onChange={setPassword}
      />
      <p className="rounded-lg bg-slate-950/80 px-3 py-2 text-xs text-slate-400">
        Demo admin: ada@exacta.test / ExactaDemo1!
      </p>
    </AuthLayout>
  );
}
