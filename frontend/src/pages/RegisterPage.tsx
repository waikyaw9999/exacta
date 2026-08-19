import { type FormEvent, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { register } from "../api/auth";
import { getApiErrorMessage } from "../api/errors";
import { AuthField, AuthLayout } from "../components/auth/AuthLayout";
import { useAuthStore } from "../stores/authStore";

export default function RegisterPage() {
  const accessToken = useAuthStore((state) => state.accessToken);
  const setSession = useAuthStore((state) => state.setSession);
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [hourlyRate, setHourlyRate] = useState("200");
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
      const rate = Number(hourlyRate);
      const response = await register({
        name,
        email,
        password,
        hourlyRate: Number.isFinite(rate) ? rate : 0,
      });
      setSession(response.accessToken, response.user);
      navigate("/", { replace: true });
    } catch (err) {
      setError(getApiErrorMessage(err, "Could not create the account"));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <AuthLayout
      title="Create account"
      subtitle="The first user on a fresh database becomes an admin."
      submitLabel="Create account"
      isSubmitting={isSubmitting}
      error={error}
      onSubmit={(event) => {
        void handleSubmit(event);
      }}
      footer={
        <>
          Already have an account?{" "}
          <Link to="/login" className="text-blue-300 hover:text-blue-200">
            Sign in
          </Link>
        </>
      }
    >
      <AuthField id="name" label="Full name" value={name} autoComplete="name" onChange={setName} />
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
        autoComplete="new-password"
        minLength={8}
        onChange={setPassword}
      />
      <AuthField
        id="hourlyRate"
        label="Hourly rate (USD)"
        type="number"
        step="0.01"
        value={hourlyRate}
        required={false}
        onChange={setHourlyRate}
      />
    </AuthLayout>
  );
}
