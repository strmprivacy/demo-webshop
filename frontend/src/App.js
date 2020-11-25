import React, {Component} from 'react';
import data from './data.json';
import Products from './components/Products';
import Filter from './components/Filter';
import Cart from './components/Cart';
import _ from 'lodash';
class App extends Component {
  constructor(){
    super();
    this.state ={
      products: data.products,
      size: "",
      sort: "",
      cartItems:localStorage.getItem("cartItems")? JSON.parse(localStorage.getItem("cartItems")):[]
    }
  }
  sendCartEvent(product, oc) {
    let uri = `/frontend-events/cart`;
    let event = {
      "product": product.id+"",
      "action": oc
    };
    fetch(uri, {
      method: 'post',
      body: JSON.stringify(event),
      headers: {
        "Content-Type": "application/json; charset=utf-8"
      }
    }).then((response)=> {
      console.log(uri, event, response.statusText);
    });

  }
  createOrder = (order) => {
    alert('Need to save order for' + order.name)
  }
  removFromCart =(item) => {
    this.sendCartEvent(item, "remove");
    const cartItems = this.state.cartItems.slice();
    this.setState({cartItems:cartItems.filter((x)=> x.id !== item.id)});
    localStorage.setItem("cartItems", JSON.stringify(this.state.cartItems));
  }
  addToCart = (product) =>{
    this.sendCartEvent(product, "add");
    const cartItems = this.state.cartItems.slice();
    let alreadyInCart =false;
    cartItems.forEach(item => {
      if(item.id === product.id){
        item.count++;
        alreadyInCart = true;
      }
    });
    if(!alreadyInCart){
      cartItems.push({...product, count: 1});
    }
    this.setState({cartItems});
    localStorage.setItem("cartItems", JSON.stringify(cartItems));
  }
  filterProducts = (event) => {
    console.log(event.target.value);
    if(event.target.value === ""){
      this.setState({
        size:event.target.value,
        products: data.products
      })
    }else{
      this.setState({
        size: event.target.value,
        products: data.products.filter(product => product.availableSizes.indexOf(event.target.value)>=0)
      })
    }
  }
  sortProducts =(event) => {
    const sort= event.target.value;
    console.log(event.target.value);
    this.setState((state) =>({
      sort:sort,
      products: this.state.products.slice().sort((a,b) =>{
        return sort === "lowest"
        ? a.price > b.price
        ? 1
        :-1
        : sort === "highest"
        ? a.price < b.price
        ? 1
        :-1
        : a.id > b.id
        ? 1
        :-1
      })
    }));
  }
  render(){
    return(
      <div className="grid-container">
        <header>
          <a href="/">Ultimate Store</a>
        </header>
        <main>
          <div className="content">
            <div className="main">
              <Filter count={this.state.products.length} size={this.state.size} filterProducts={this.filterProducts} sort={this.state.sort} sortProducts={this.sortProducts}/>
              <Products product={this.state.products} addToCart={this.addToCart}/>
            </div>
            <div className="sidebar">
                <Cart cartItems={this.state.cartItems} removFromCart={this.removFromCart} createOrder={this.createOrder}/>
            </div>
          </div>
        </main>
        <footer>
          All rights reserved
        </footer>
      </div>
    )
  }
}

export default App;
