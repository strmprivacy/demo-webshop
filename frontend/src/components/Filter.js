import React, { Component } from 'react'

class Filter extends Component {
    render() {
        return (
            <div className="filter">
                <div className="filter-result">{this.props.count} results</div>  
                <div className="filter-order"> Order {""} 
                    <select value={this.props.sort} onChange={this.props.sortProducts}>
                        <option>Latest</option>
                        <option value="lowest">Lowest</option>
                        <option value="highest">highest</option>
                    </select>
                </div> 
                <div className="filter-size"> Size {""} 
                    <select value={this.props.size} onChange={this.props.filterProducts}>
                        <option value="">ALL</option>
                        <option value="S">S</option>
                        <option value="M">M</option>
                        <option value="L">L</option>
                        <option value="XL">XL</option>
                        <option value="XXL">XXL</option>
                    </select>
                </div>           
            </div>
        )
    }
}

export default Filter
