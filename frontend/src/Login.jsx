import axios from 'axios'
import React, { useState } from 'react'

function Login() {
    const [tokens, setTokens] = useState({})
    const [error, setError] = useState({})
    const [loginForm, setLoginForm] = useState({
        email: '',
        password: ''
    })

    const handleChange = (event) => {
        const { name, value } = event.target
        setLoginForm((currentForm) => {
            return { ...currentForm, [name]: value }
        })
    }

    const handleSubmit = (event) => {
        event.preventDefault()
        axios.post('...', {
            email: loginForm.email,
            password: loginForm.password
        })
            .then(response => {
                setTokens(response)
            })
            .catch(error => {
                setError(error)
            })
    }

    return (
        <div>
            <div> Log in </div>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor='email'>Email: </label>
                    <input
                        type='text'
                        id='email'
                        name='email'
                        value={loginForm.email}
                        placeholder='email'
                        onChange={handleChange}
                    />
                </div>
                <div>
                    <label htmlFor='password'>Password: </label>
                    <input
                        type='password'
                        id='password'
                        name='password'
                        value={loginForm.password}
                        placeholder='password'
                        onChange={handleChange}
                    />
                </div>
            </form>
        </div>
    )
}

export default Login
