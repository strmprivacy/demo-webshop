import React, { Component } from 'react'
import Formatcurrency from './util';
class Cart extends Component {
    constructor(){
        super();
        this.state={
            showCheckout:false,
            name:"",
            email:"",
            address:"",
        }
    }
    handleInput = (e) => {
        this.setState({[e.target.name]: e.target.value});
    }
    createOrder = (e) => {
        e.preventDefault();
        const order = {
            name: this.state.name,
            email: this.state.email,
            address: this.state.address,
            cartItems: this.props.cartItems
        };
        // for save order //
        this.props.createOrder(order)
    }
    render() {
        const {cartItems}= this.props;
        return (
            <>
                <div className="cartItems">
                    {cartItems.length === 0? (<p>You have 0 items in the cart</p>)
                    :
                    (<p>You have {cartItems.length} items in the cart</p>)}
                </div>
                <ul className="cartItemsList">
                    {cartItems.map(item => 
                    <li key={item.id}>
                        <div className="itemImage">
                            <img src={item.image} alt={item.title}/>
                        </div>
                        <div className="itemDetail">
                            <p>{item.title}</p>
                            <div className="itemTotal">
                                <span>Total: {Formatcurrency(item.price)} x {item.count}</span>
                                <button type="button" className="button primary" onClick={() => this.props.removFromCart(item)}>Remove</button>
                            </div>
                        </div>
                    </li>
                    )}
                </ul>
                {cartItems.length !== 0 && (<div className="cartProceed">
                    <span>Total:{''} {Formatcurrency(cartItems.reduce((a,c) => a + c.price * c.count, 0))}</span>
                    <button type="button" className="button primary" onClick={()=>{this.setState({showCheckout:true})}}>Proceed</button>
                </div>)}
                {this.state.showCheckout && (
                    <div className="cart">
                        <form onSubmit={this.createOrder}>
                            <ul className="formContainer">
                                <li>
                                    <label>Email</label>
                                    <input type="email" name="email" required onChange={this.handleInput} />
                                </li>
                                <li>
                                    <label>Name</label>
                                    <input type="text" name="name" required onChange={this.handleInput} />
                                </li>
                                <li>
                                    <label>Address</label>
                                    <input type="text" name="address" onChange={this.handleInput} />
                                </li>
                                <li>
                                    <button type="submit" className="button primary">Checkout</button>
                                </li>
                            </ul>
                        </form>
                    </div>
                )}
            </>
        )
    }
}

export default Cart